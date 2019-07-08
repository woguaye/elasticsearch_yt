package com.jty.performance.service.auth.impl;

import com.jty.performance.domain.*;
import com.jty.performance.domain.auth.AuthRole;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.auth.KPIManagerHistory;
import com.jty.performance.domain.dto.auth.KPIManagerDTO;
import com.jty.performance.domain.dto.auth.KPIManagerHistoryDto;
import com.jty.performance.domain.form.auth.KPIManagerHistoryForm;
import com.jty.performance.domain.form.auth.KPIManagerSearchForm;
import com.jty.performance.domain.form.auth.KPIStaffForm;
import com.jty.performance.enums.KPIManagerOperateItemEnums;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.*;
import com.jty.performance.repository.auth.RoleRepository;
import com.jty.performance.repository.auth.UserRepository;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.auth.KPIManagerService;
import com.jty.performance.support.ResultCode;
import com.jty.performance.util.PoCastUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * @Author: yeting
 * @Date: 2019/5/22 14:19
 */
@Service
public class KPIManagerServiceImpl implements KPIManagerService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KPIManagerHistoryRepository managerHistoryRepository;

    @Autowired
    private PlanCycleRepository planCycleRepository;

    @Autowired
    private IntegralHistoryListRepository integralHistoryListRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Page<KPIManagerDTO> getManagers(MyUserDetail user, KPIManagerSearchForm form) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(Sort.Order.desc("staffCode"));
        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize(), Sort.by(sortOrders));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (!StringUtils.isEmpty(form.getAccount())) {
                    expressions.add(criteriaBuilder.or(criteriaBuilder.like(root.get("staffCode"), "%" + form.getAccount() + "%"), criteriaBuilder.like(root.get("eName"), "%" + form.getAccount() + "%")));
                }
                if (form.getEnabled() != null) {
                    expressions.add(criteriaBuilder.equal(root.get("authUser").get("enabled"), form.getEnabled()));
                }
                Join<AuthUser, Employee> join = root.join("authUser", JoinType.INNER);
                if (form.getRoleId() != null) {
                    expressions.add(criteriaBuilder.in(join.join("roles").get("id")).value(form.getRoleId()));
                }
                if (!StringUtils.isEmpty(form.getDeptId())) {
                    String[] deptIds = form.getDeptId().split(",");
                    Predicate[] predicates = new Predicate[deptIds.length];
                    for (int i = 0; i < deptIds.length; i++) {
                        Predicate equal = criteriaBuilder.equal(root.get("id"), deptIds[i]);
                        predicates[i] = equal;
                    }
                    expressions.add(criteriaBuilder.or(predicates));
                } else if (!"admin".equals(user.getUsername())) {
                    Department department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
                    expressions.add(criteriaBuilder.equal(root.get("department").get("parentId"), department.getParentId()));
                }
                expressions.add(criteriaBuilder.notEqual(root.get("id"), user.getUserId()));
                return predicate;
            }
        };
        Page<Employee> page = employeeRepository.findAll(spec, pageable);
        List<Employee> employees = page.getContent();
        List<KPIManagerDTO> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(employees)) {
            for (Employee employee : employees) {
                KPIManagerDTO kpiManagerDTO = employee.castToManagerDto();
                dtos.add(kpiManagerDTO);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void addStaff(KPIStaffForm form) {
        AuthUser authUser = userRepository.findByAccount(form.getAccount()).orElse(null);
        if (authUser != null) {
            Employee employee = employeeRepository.findByAuthUser(authUser).orElse(null);
            if (employee != null) {
                throw new BusinessException(ResultCode.USER_HAS_EXISTED);
            }

        } else {
            authUser = new AuthUser();
            authUser.setAccount(form.getAccount());
            authUser.setEnabled(true);
            authUser.setPassword(passwordEncoder.encode("123456"));
            if (CollectionUtils.isNotEmpty(form.getRoleIds())) {
                for (Integer roleId : form.getRoleIds()) {
                    authUser.addRole(roleRepository.findById(roleId).orElse(null));
                }
            }
            userRepository.save(authUser);
        }
        Employee employee = new Employee();
        employee.setId(authUser.getId());
        employee.setAuthUser(authUser);
        employee.setCurrentScore(0);
        employee.setEName(form.getRealName());
        employee.setEPosition(form.getPosition());
        employee.setRecordScore(0);
        employee.setStaffCode(form.getAccount());
        Department department = departmentRepository.findById(form.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        employee.setDepartment(department);
        employeeRepository.save(employee);
        PlanCycle planCycle = getPlanCycleInfo(form.getDeptId());
        if (planCycle.getCount() > 1) {
            PlanCycle histPlanCycle = planCycleRepository.findByCountAndDepartment(planCycle.getCount() - 1, planCycle.getDepartment()).orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
            Integer rank = integralHistoryListRepository.findByPlanCycleIdAndRankMax(histPlanCycle.getId());
            IntegralHistoryList integralHistoryList = new IntegralHistoryList();
            integralHistoryList.setPlanCycleId(histPlanCycle.getId());
            integralHistoryList.setEmployee(employee);
            integralHistoryList.setRank(rank + 1);
            integralHistoryList.setScore(0);
            integralHistoryList.setDepartment(employee.getDepartment());
            integralHistoryListRepository.save(integralHistoryList);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateStaff(KPIStaffForm form) {
        Employee employee = employeeRepository.findById(form.getUserId()).orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        Department department = departmentRepository.findById(form.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
//        //查询插入部门的考核期
//        Department parentDept = new Department();
//        parentDept.setId(department.getParentId());
//        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
//        if (planCycle.getCount() > 1) {
//            PlanCycle histPlanCycle = planCycleRepository.findByCountAndDepartment(planCycle.getCount() - 1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
//            Integer rankMax = integralHistoryListRepository.findByPlanCycleIdAndRankMax(histPlanCycle.getId());
//            IntegralHistoryList integralHistoryList = new IntegralHistoryList();
//            integralHistoryList.setPlanCycleId(histPlanCycle.getId());
//            integralHistoryList.setEmployee(employee);
//            integralHistoryList.setRank(rankMax + 1);
//            integralHistoryList.setScore(0);
//            integralHistoryList.setDepartment(department);
//            integralHistoryListRepository.save(integralHistoryList);
//        }
//        employee.setRecordScore(employee.getCurrentScore() + employee.getRecordScore());
        employee.setEName(form.getRealName());
        employee.setEPosition(form.getPosition());
        employee.setStaffCode(form.getAccount());
        employee.setDepartment(department);
//        employee.setCurrentScore(0);
        AuthUser authUser = employee.getAuthUser();
        authUser.setAccount(form.getAccount());
        if (CollectionUtils.isNotEmpty(form.getRoleIds())) {
            authUser.getRoles().clear();
            form.getRoleIds().forEach(roleId -> {
                authUser.addRole(roleRepository.findById(roleId).orElse(null));
            });
        }
        userRepository.save(authUser);
        employeeRepository.save(employee);
    }

    @Override
    public void resetPassword(Integer staffId) {
        String password = "123456";
        AuthUser user = userRepository.findById(staffId).orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public void enableManager(Integer staffId, Integer userId, String operateReason) {
        AuthUser user = userRepository.findById(staffId).orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        user.setEnabled(true);
        userRepository.save(user);
        this.addHistory(staffId, userId, KPIManagerOperateItemEnums.ENABLE.getDocs(), operateReason);
    }

    @Override
    public void disableManager(Integer staffId, Integer userId, String operateReason) {
        if (staffId.equals(userId)) {
            throw new BusinessException(ResultCode.MISOPERATION);
        }
        Employee employee = new Employee();
        employee.setId(staffId);
        List<Task> allTaskById = taskRepository.findByEmployee(employee);
        for (Task task : allTaskById) {
            if (!"5".equals(task.getTaskState() + "")) {
                throw new BusinessException(ResultCode.STAFF_TASK_UNDONE);
            }
        }
        AuthUser user = userRepository.findById(staffId).orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        user.setEnabled(false);
        userRepository.save(user);

        this.addHistory(staffId, userId, KPIManagerOperateItemEnums.DISABLE.getDocs(), operateReason);
    }

    @Override
    public Page<KPIManagerHistoryDto> findHistoryByManagerId(Integer staffId, KPIManagerHistoryForm form) {
        KPIManagerHistory managerHistory = KPIManagerHistory.builder().managerId(staffId).build();
        form.setOrderBy("modifiedTime");
        form.setDirection("desc");
        Page<KPIManagerHistory> page = managerHistoryRepository.findAll(Example.of(managerHistory), form.getPageable());
        return PoCastUtils.poPageCastToDto(page, KPIManagerHistoryDto.class);
    }

    @Override
    public void deleteStaff(Integer staffId) {
        Employee employee = employeeRepository.findById(staffId).orElse(null);
        AuthUser authUser = userRepository.findById(staffId).orElse(null);
        if (employee != null && authUser != null) {
            employeeRepository.delete(employee);
            userRepository.delete(authUser);
        } else {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
    }

    private void addHistory(Integer managerId, Integer operatorId, String operateItem, String operateReason) {
        Employee employee = employeeRepository.findById(operatorId).orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        KPIManagerHistory managerHistory = new KPIManagerHistory();
        managerHistory.setManagerId(managerId);
        managerHistory.setOperateReason(operateReason);
        managerHistory.setOperation(operateItem);
        managerHistory.setOperatorId(operatorId);
        managerHistory.setOperatorName(employee.getEName());
        managerHistoryRepository.save(managerHistory);
    }

    private PlanCycle getPlanCycleInfo(Integer deptId) {
        Department department = departmentRepository.findById(deptId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department parentDept = new Department();
        parentDept.setId(department.getParentId());
        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        return planCycle;
    }

    /**
     * 获取当前积分排名
     *
     * @return
     */
    private Map<Integer, Integer> getEmployeeScoreRank(Integer partentId) {
        HashMap<Integer, Integer> map = new HashMap<>();
        List<Employee> orderByCurrentScore = employeeRepository.findOrderByCurrentScore(partentId);
        Integer rank = 1;
        for (Employee employee : orderByCurrentScore) {
            map.put(employee.getId(), rank++);
        }
        return map;
    }
}
