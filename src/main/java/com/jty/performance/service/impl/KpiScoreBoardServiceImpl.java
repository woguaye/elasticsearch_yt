package com.jty.performance.service.impl;

import com.jty.performance.domain.*;
import com.jty.performance.domain.auth.AuthRole;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.dto.KpiPlanCycleDto;
import com.jty.performance.domain.dto.KpiScoreBoardDto;
import com.jty.performance.domain.form.KpiScoreBoardForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.*;
import com.jty.performance.repository.auth.UserRepository;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiPlanCycleService;
import com.jty.performance.service.KpiScoreBoardService;
import com.jty.performance.support.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * @Author: yeting
 * @Date: 2019/4/26 21:53
 */
@Service
public class KpiScoreBoardServiceImpl implements KpiScoreBoardService {


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LogIntegralRepository logIntegralRepository;

    @Autowired
    private IntegralHistoryListRepository integralHistoryListRepository;

    @Autowired
    private KpiPlanCycleService kpiPlanCycleService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanCycleRepository planCycleRepository;

    @Override
    public Page<KpiScoreBoardDto> getScoreBoard(MyUserDetail user, KpiScoreBoardForm form) {
        AuthUser authUser = userRepository.findById(user.getUserId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        List<AuthRole> roles = authUser.getRoles();
        Boolean flag = false;
        if (CollectionUtils.isNotEmpty(roles)) {
            for (AuthRole authRole : roles) {
                if ("5".equals(authRole.getId() + "")) {
                    flag = true;
                }
            }
        }
        Map<Integer, Integer> sortMap;
        Department department;
        KpiPlanCycleDto planCycleInfo;
        if (flag) {
            if (form.getDeptId() != null) {
                sortMap = getEmployeeScoreRankByParentId(form.getDeptId());
                department = departmentRepository.findAllByParentId(form.getDeptId()).get(0);
                planCycleInfo = getPlanCycleInfoByDeptParent(form.getDeptId());
            } else {
                sortMap = getEmployeeScoreRank(2);
                department = departmentRepository.findById(2).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
                planCycleInfo = getPlanCycleInfoByDept(department);
            }
        } else {
            sortMap = getEmployeeScoreRank(user.getDeptId());
            department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
            planCycleInfo = kpiPlanCycleService.getPlanCycleInfo(user);
        }
        if (!planCycleInfo.getPlanCycleId().equals(form.getPlanCycleId()) && form.getPlanCycleId() != null) {
            return getScoreBoardSortByHistoryList(department, planCycleInfo, form);
        }
        String orderBy = form.getOrderBy();
        String direction = form.getDirection();
        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize());
        Boolean finalFlag = flag;
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (form.getUserId() != null && !"".equals(form.getUserId())) {
                    expressions.add(criteriaBuilder.equal(root.get("id"), form.getUserId()));
                }
                Join<IntegralHistoryList, Employee> join = root.join("integralHistoryList", JoinType.LEFT);
                if (planCycleInfo.getCount() != 1) {
                    Department parentDept = new Department();
                    parentDept.setId(department.getParentId());
                    PlanCycle planCycle = planCycleRepository.findByCountAndDepartment(planCycleInfo.getCount() - 1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
                    expressions.add(criteriaBuilder.equal(join.get("planCycleId"), planCycle.getId()));
                }
                Join<AuthUser, Employee> userLeftEms = root.join("authUser", JoinType.LEFT);
                expressions.add(criteriaBuilder.equal(userLeftEms.join("roles").get("id"), 2));
                expressions.add(criteriaBuilder.equal(userLeftEms.get("enabled"), true));
                expressions.add(criteriaBuilder.equal(root.join("department").get("parentId"), department.getParentId()));
                criteriaQuery.where(predicate);
                ArrayList<Order> orders = new ArrayList<>();
                if (orderBy == null || "".equals(orderBy) || "quarterIntegra".equals(orderBy)) {
                    if (direction == null || "".equals(direction) || "desc".equals(direction)) {
                        orders.add(criteriaBuilder.desc(root.get("currentScore")));
                        orders.add(criteriaBuilder.desc(root.get("recordScore")));
                        orders.add(criteriaBuilder.asc(root.get("staffCode")));
                    } else {
                        orders.add(criteriaBuilder.asc(root.get("currentScore")));
                        orders.add(criteriaBuilder.asc(root.get("recordScore")));
                        orders.add(criteriaBuilder.desc(root.get("staffCode")));
                    }
                } else if ("qualification".equals(orderBy)) {
                    if (direction == null || "".equals(direction) || "desc".equals(direction)) {
                        orders.add(criteriaBuilder.desc(root.get("recordScore")));
                        orders.add(criteriaBuilder.asc(root.get("staffCode")));
                    } else {
                        orders.add(criteriaBuilder.asc(root.get("recordScore")));
                        orders.add(criteriaBuilder.desc(root.get("staffCode")));
                    }
                } else if ("preQuarterIntegra".equals(orderBy)) {
                    if (direction == null || "".equals(direction) || "desc".equals(direction)) {
                        orders.add(criteriaBuilder.asc(join.get("score")));
                        orders.add(criteriaBuilder.desc(root.get("recordScore")));
                        orders.add(criteriaBuilder.asc(root.get("staffCode")));
                    } else {
                        orders.add(criteriaBuilder.desc(join.get("score")));
                        orders.add(criteriaBuilder.asc(root.get("recordScore")));
                        orders.add(criteriaBuilder.desc(root.get("staffCode")));
                    }
                }
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
//                return predicate;
            }
        };
        Page<Employee> page = employeeRepository.findAll(spec, pageable);
        List<Employee> Employees = page.getContent();
        List<KpiScoreBoardDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(Employees)) {
            for (Employee employee : Employees) {
                KpiScoreBoardDto dto = employee.castToDto(planCycleInfo.getCount());
                dto.setRank(sortMap.get(employee.getId()));
                dtos.add(dto);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }

    /**
     * 获取当期积分排名
     *
     * @return
     */
    private Map<Integer, Integer> getEmployeeScoreRank(Integer deptId) {
        Department department = departmentRepository.findById(deptId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        HashMap<Integer, Integer> map = new HashMap<>();
        List<Employee> orderByCurrentScore = employeeRepository.findOrderByCurrentScore(department.getParentId());
        Integer rank = 1;
        for (Employee employee : orderByCurrentScore) {
            map.put(employee.getId(), rank++);
        }
        return map;
    }

    private Map<Integer, Integer> getEmployeeScoreRankByParentId(Integer parentId) {
        HashMap<Integer, Integer> map = new HashMap<>();
        List<Employee> orderByCurrentScore = employeeRepository.findOrderByCurrentScore(parentId);
        Integer rank = 1;
        for (Employee employee : orderByCurrentScore) {
            map.put(employee.getId(), rank++);
        }
        return map;
    }

    /**
     * 根据上期积分排行
     *
     * @param department
     * @param planCycleInfo
     * @param form
     * @return
     */
    private Page<KpiScoreBoardDto> getScoreBoardSortByHistoryList(Department department, KpiPlanCycleDto planCycleInfo, KpiScoreBoardForm form) {
        String orderBy = form.getOrderBy();
        String direction = form.getDirection();
        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize());
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                expressions.add(criteriaBuilder.equal(root.get("planCycleId"), form.getPlanCycleId()));
                Join<Employee, IntegralHistoryList> join = root.join("employee", JoinType.INNER);
                Join<AuthUser, Employee> userLeftEms = join.join("authUser", JoinType.INNER);
                expressions.add(criteriaBuilder.equal(userLeftEms.join("roles").get("id"), 2));
                Join<Employee, IntegralHistoryList> joinEmpl = join.join("integralHistoryList", JoinType.INNER);
                if (form.getPlanCycleId() != 1) {
                    Department parentDept = new Department();
                    parentDept.setId(department.getParentId());
                    PlanCycle planCycle = planCycleRepository.findByCountAndDepartment(planCycleInfo.getCount() - 1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
                    expressions.add(criteriaBuilder.equal(joinEmpl.get("planCycleId"), planCycle.getId()));
                } else {
                    expressions.add(criteriaBuilder.equal(joinEmpl.get("planCycleId"), form.getPlanCycleId()));
                }
                Join<Employee, LogQualification> qualJoin = join.join("logQualification", JoinType.INNER);
                expressions.add(criteriaBuilder.equal(qualJoin.get("planCycle"), form.getPlanCycleId()));
                criteriaQuery.where(predicate);
                ArrayList<Order> orders = new ArrayList<>();

                if (orderBy == null || "".equals(orderBy) || "quarterIntegra".equals(orderBy)) {
                    if (direction == null || "".equals(direction) || "desc".equals(direction)) {
                        orders.add(criteriaBuilder.asc(root.get("rank")));
                    } else {
                        orders.add(criteriaBuilder.desc(root.get("rank")));
                    }
                } else if ("qualification".equals(orderBy)) {
                    if (direction == null || "".equals(direction) || "desc".equals(direction)) {
                        orders.add(criteriaBuilder.desc(qualJoin.get("qualifModifyNum")));
                    } else {
                        orders.add(criteriaBuilder.asc(qualJoin.get("qualifModifyNum")));
                    }
                } else if ("preQuarterIntegra".equals(orderBy)) {
                    if (direction == null || "".equals(direction) || "desc".equals(direction)) {
                        orders.add(criteriaBuilder.asc(joinEmpl.get("rank")));
                    } else {
                        orders.add(criteriaBuilder.desc(joinEmpl.get("rank")));
                    }
                }
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        Page<IntegralHistoryList> page = integralHistoryListRepository.findAll(spec, pageable);
        List<IntegralHistoryList> integralHistoryList = page.getContent();
        List<KpiScoreBoardDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(integralHistoryList)) {
            for (IntegralHistoryList integralHistory : integralHistoryList) {
                KpiScoreBoardDto dto = integralHistory.castToDto(form.getPlanCycleId());
                dtos.add(dto);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }


    @Override
    public KpiScoreBoardDto getUserInfo(Integer userId) {
        Sort orders = new Sort(Sort.Direction.DESC, "currentScore");
        List<Employee> Employees = employeeRepository.findAll(orders);
        Integer rank = 1;
        KpiScoreBoardDto dto = null;
        for (Employee employee : Employees) {
            dto = employee.castToDtoInfo();
            dto.setRank(rank++);
            if (userId.equals(employee.getId())) {
                break;
            }
        }
        return dto;
    }

    private KpiPlanCycleDto getPlanCycleInfoByDept(Department department) {
        Department parentDept = new Department();
        parentDept.setId(department.getParentId());
        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        return planCycle.castToDto();
    }

    private KpiPlanCycleDto getPlanCycleInfoByDeptParent(Integer parentId) {
        Department parentDept = new Department();
        parentDept.setId(parentId);
        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        return planCycle.castToDto();
    }
}
