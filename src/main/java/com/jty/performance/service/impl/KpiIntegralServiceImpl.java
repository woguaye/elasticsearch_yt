package com.jty.performance.service.impl;

import com.jty.performance.domain.*;
import com.jty.performance.domain.auth.AuthAuthority;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.dto.*;
import com.jty.performance.domain.form.AcceptEmployeeForm;
import com.jty.performance.domain.form.EmployeeAccForm;
import com.jty.performance.domain.form.KpiScoreBoardForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.*;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiIntegralService;
import com.jty.performance.service.KpiPlanCycleService;
import com.jty.performance.support.ResultCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * @Author: yeting
 * @Date: 2019/4/28 22:32
 */
@Service
public class KpiIntegralServiceImpl implements KpiIntegralService {

    @Autowired
    private LogIntegralRepository logIntegralRepository;

    @Autowired
    private LogQualificationRepository logQualificationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PlanCycleRepository planCycleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LogStaffSignRepository logStaffSignRepository;

    @Autowired
    private ParameterRepository parameterRepository;


    @Override
    public Page<KpiIntegralDto> findIntegralInfo(KpiScoreBoardForm form) {
//        PlanCycle planCycle = planCycleRepository.findByPlanCycleState(1).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(Sort.Order.desc("integModifyTime"));
        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize(), Sort.by(sortOrders));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (form.getUserId() != null && !"".equals(form.getUserId())) {
                    expressions.add(criteriaBuilder.equal(root.get("employee"), form.getUserId()));
                }
                expressions.add(criteriaBuilder.equal(root.get("planCycle"), form.getPlanCycleId()));
                if (!"5".equals(form.getScoreTypeId() + "")) {
                    expressions.add(criteriaBuilder.notEqual(root.get("integralModifyType"), 5));
                }
                return predicate;
            }
        };
        Page<LogIntegral> page = logIntegralRepository.findAll(spec, pageable);
        List<LogIntegral> logIntegrals = page.getContent();
        List<KpiIntegralDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(logIntegrals)) {
            for (LogIntegral logIntegral : logIntegrals) {
                KpiIntegralDto kpiIntegralDto = logIntegral.castToDto();
                dtos.add(kpiIntegralDto);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }


    @Override
    public Page<KpiIntegralDto> findRecordInfo(KpiScoreBoardForm form) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(Sort.Order.desc("qualifModifyTime"));
        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize(), Sort.by(sortOrders));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (form.getUserId() != null && !"".equals(form.getUserId())) {
                    expressions.add(criteriaBuilder.equal(root.get("employee"), form.getUserId()));
                }
                return predicate;
            }
        };
        Page<LogQualification> page = logQualificationRepository.findAll(spec, pageable);
        List<LogQualification> logQualifications = page.getContent();
        List<KpiIntegralDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(logQualifications)) {
            for (LogQualification logQualification : logQualifications) {
                KpiIntegralDto kpiIntegralDto = logQualification.castToDto();
                dtos.add(kpiIntegralDto);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }


    @Override
    public List<StaffNodeDto> findEmployeeNodes(MyUserDetail user) {
        ArrayList<StaffNodeDto> staffNodeDtos = new ArrayList<>();
        List<Department> allDepartment = departmentRepository.findAll();
        if (CollectionUtils.isNotEmpty(allDepartment)) {
            for (Department department : allDepartment) {
                StaffNodeDto staffNodeDto = new StaffNodeDto();
                staffNodeDto.setId(department.getId());
                staffNodeDto.setName(department.getDeptName());
                staffNodeDto.setParentId(department.getParentId());
                List<StaffNodeDto> childs = getChilds(allDepartment, department.getId());
                List<KpiEmployeeDto> employeeDtoList = getEmployeeDtos(department.getId());
                staffNodeDto.setEmployeeList(employeeDtoList);
                staffNodeDto.setChildren(childs);
                if ("0".equals(department.getParentId() + "")) {
                    staffNodeDtos.add(staffNodeDto);
                }
            }
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();
            if ("KPI_MANAGE_SYSTEM".equals(authority) && "admin".equals(user.getUsername())) {
                return staffNodeDtos;
            }
            if ("KPI_MANAGE_DEPARTMENT".equals(authority)) {
                for (StaffNodeDto staffNodeDto : staffNodeDtos) {
                    if (user.getDeptId().equals(staffNodeDto.getId())) {
                        staffNodeDtos.clear();
                        staffNodeDtos.add(staffNodeDto);
                        return staffNodeDtos;
                    }
                }
            }
        }
        Department department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        for (StaffNodeDto staffNodeDto : staffNodeDtos) {
            if (department.getParentId().equals(staffNodeDto.getId())) {
                staffNodeDtos.clear();
                staffNodeDtos.add(staffNodeDto);
                return staffNodeDtos;
            }
        }
        return null;
    }

    /**
     * 查询部门下的所有员工
     *
     * @param id
     * @return
     */
    private List<KpiEmployeeDto> getEmployeeDtos(Integer id) {
        Department department = new Department();
        department.setId(id);
        List<Employee> byDepartment = employeeRepository.findByDepartment(department);
        ArrayList<KpiEmployeeDto> kpiEmployeeDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(byDepartment)) {
            for (Employee employee : byDepartment) {
                KpiEmployeeDto kpiEmployeeDto = new KpiEmployeeDto();
                kpiEmployeeDto.setId(employee.getId());
                kpiEmployeeDto.setName(employee.getEName());
                kpiEmployeeDtos.add(kpiEmployeeDto);
            }
        }

        return kpiEmployeeDtos;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void divideScoreToEmployee(MyUserDetail user, Integer eId, AcceptEmployeeForm acceptEmployee) {
        Employee employee = employeeRepository.findById(eId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department department = new Department();
        department.setId(employee.getDepartment().getParentId());
        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, department).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        //当前的积分值
        Integer currentScore = employee.getCurrentScore();
        List<EmployeeAccForm> employeeAcceptList = acceptEmployee.getEmployeeAcceptList();
        Integer presentScore = 0;
        if (CollectionUtils.isNotEmpty(employeeAcceptList)) {
            for (EmployeeAccForm employeeAcc : employeeAcceptList) {
                presentScore += employeeAcc.getAcceptScore();
            }
            if (presentScore > currentScore) {
                throw new BusinessException(ResultCode.SCORE_BEYOND);
            } else {
                if (CollectionUtils.isNotEmpty(employeeAcceptList)) {
                    for (EmployeeAccForm employeeAcc : employeeAcceptList) {
                        Employee acceptEmp = employeeRepository.findById(employeeAcc.getAcceptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
                        acceptEmp.setCurrentScore(acceptEmp.getCurrentScore() + employeeAcc.getAcceptScore());
                        employeeRepository.save(acceptEmp);
                        LogIntegral logIntegral = new LogIntegral();
                        logIntegral.setEmployee(acceptEmp);
                        logIntegral.setIntegModifyNum(employeeAcc.getAcceptScore());
                        logIntegral.setIntegModifyReason(employeeAcc.getAcceptReason() + "(来源：" + employee.getEName() + ")");
                        IntegralModifyType integralModifyType = new IntegralModifyType();
                        integralModifyType.setId(2);
                        logIntegral.setIntegralModifyType(integralModifyType);
                        logIntegral.setPlanCycle(planCycle);
                        logIntegralRepository.save(logIntegral);

                        LogIntegral pLogIntegral = new LogIntegral();
                        pLogIntegral.setEmployee(employee);
                        pLogIntegral.setIntegModifyNum(-employeeAcc.getAcceptScore());
                        pLogIntegral.setIntegModifyReason(employeeAcc.getAcceptReason() + "(接收人：" + acceptEmp.getEName() + ")");
                        IntegralModifyType pIntegralModifyType = new IntegralModifyType();
                        pIntegralModifyType.setId(2);
                        pLogIntegral.setIntegralModifyType(pIntegralModifyType);
                        pLogIntegral.setPlanCycle(planCycle);
                        logIntegralRepository.save(pLogIntegral);

                    }
                    employee.setCurrentScore(employee.getCurrentScore() - presentScore);
                    employeeRepository.save(employee);
                }
            }

        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void divideTaskScoreToEmployee(MyUserDetail user, Integer taskId, AcceptEmployeeForm acceptEmployee) {
        Department department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department parentDept = new Department();
        parentDept.setId(department.getParentId());
        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        List<EmployeeAccForm> employeeAcceptList = acceptEmployee.getEmployeeAcceptList();
        if (CollectionUtils.isNotEmpty(employeeAcceptList)) {
            Integer presentScore = 0;
            for (EmployeeAccForm employeeAcc : employeeAcceptList) {
                presentScore += employeeAcc.getAcceptScore();
            }
            if (!presentScore.equals(task.getActualScore())) {
                throw new BusinessException(ResultCode.TASK_SCORE_BEYOND);
            }
            task.getEmployees().clear();
            for (EmployeeAccForm employeeAccForm : employeeAcceptList) {
                Employee employee = employeeRepository.findById(employeeAccForm.getAcceptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
                LogIntegral logIntegral = new LogIntegral();
                logIntegral.setEmployee(employee);
                logIntegral.setIntegModifyNum(employeeAccForm.getAcceptScore());
                long degree = Math.round(employeeAccForm.getAcceptScore().doubleValue() / task.getActualScore().doubleValue() * 100);
                logIntegral.setIntegModifyReason("参与任务：" + task.getTaskName() + "(" + degree + "%)");
                IntegralModifyType integralModifyType = new IntegralModifyType();
                integralModifyType.setId(1);
                logIntegral.setIntegralModifyType(integralModifyType);
                logIntegral.setPlanCycle(planCycle);
                logIntegral.setTask(task);
                logIntegralRepository.save(logIntegral);
                employee.setCurrentScore(employee.getCurrentScore() + employeeAccForm.getAcceptScore());
                employeeRepository.save(employee);
                task.addEmployee(employee);
            }
        }
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        task.setCompletionTime(localDate);
        //修改任务状态 5;已完成
        task.setTaskState(5);
        taskRepository.save(task);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public KpiSignDto staffSign(Integer userId) {
        Employee employee = employeeRepository.findById(userId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department department = new Department();
        department.setId(employee.getDepartment().getParentId());
        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, department).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LogStaffSign existLogStaffSign = logStaffSignRepository.findByUserIdAndSignTime(userId, localDate).orElse(null);
        KpiSignDto kpiSignDto = new KpiSignDto();
        if (existLogStaffSign == null) {
            staffSigning(userId, planCycle);
            LogStaffSign logStaffSign = new LogStaffSign();
            logStaffSign.setSignTime(localDate);
            logStaffSign.setUserId(userId);
            logStaffSign.setPlanCycleId(planCycle.getId());
            logStaffSignRepository.save(logStaffSign);
            Parameter parameter = parameterRepository.findById(1).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
            kpiSignDto.setSignScore(parameter.getScoreSign());
        } else {
            throw new BusinessException(ResultCode.USER_CHECKED);
        }
        return kpiSignDto;
    }

    @Override
    public Page<KpiIntegralDto> findEmployeeSign(KpiScoreBoardForm form) {
        //        PlanCycle planCycle = planCycleRepository.findByPlanCycleState(1).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(Sort.Order.desc("integModifyTime"));
        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize(), Sort.by(sortOrders));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (form.getUserId() != null && !"".equals(form.getUserId())) {
                    expressions.add(criteriaBuilder.equal(root.get("employee"), form.getUserId()));
                }
                expressions.add(criteriaBuilder.equal(root.get("planCycle"), form.getPlanCycleId()));
                expressions.add(criteriaBuilder.equal(root.get("integralModifyType"), 5));
                return predicate;
            }
        };
        Page<LogIntegral> page = logIntegralRepository.findAll(spec, pageable);
        List<LogIntegral> logIntegrals = page.getContent();
        List<KpiIntegralDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(logIntegrals)) {
            for (LogIntegral logIntegral : logIntegrals) {
                KpiIntegralDto kpiIntegralDto = logIntegral.castToDto();
                dtos.add(kpiIntegralDto);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }

    private void staffSigning(Integer userId, PlanCycle planCycle) {
        LogIntegral logIntegral = new LogIntegral();
        logIntegral.setIntegModifyReason("签到");
        IntegralModifyType integralModifyType = new IntegralModifyType();
        integralModifyType.setId(5);
        logIntegral.setIntegralModifyType(integralModifyType);
        AuthUser authUser = new AuthUser();
        authUser.setId(userId);
        Employee employee = employeeRepository.findByAuthUser(authUser).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        logIntegral.setEmployee(employee);
        logIntegral.setPlanCycle(planCycle);
        Parameter parameter = parameterRepository.findById(1).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        logIntegral.setIntegModifyNum(parameter.getScoreSign());
        logIntegralRepository.save(logIntegral);
        employee.setCurrentScore(employee.getCurrentScore() + parameter.getScoreSign());
        employeeRepository.save(employee);
    }


    /**
     * 查询该部门下所有子部门
     *
     * @param allDepartment
     * @param id
     * @return
     */
    private List<StaffNodeDto> getChilds(List<Department> allDepartment, Integer id) {
        ArrayList<StaffNodeDto> childNodeDtos = new ArrayList<>();
        for (Department dept : allDepartment) {
            if (dept.getParentId().equals(id)) {
                StaffNodeDto staffNodeDto = new StaffNodeDto();
                staffNodeDto.setId(dept.getId());
                staffNodeDto.setName(dept.getDeptName());
                staffNodeDto.setParentId(dept.getParentId());
                List<KpiEmployeeDto> employeeDtos = getEmployeeDtos(dept.getId());
                staffNodeDto.setEmployeeList(employeeDtos);
                childNodeDtos.add(staffNodeDto);
                getChilds(allDepartment, dept.getId());
            }
        }
        return childNodeDtos;
    }
}
