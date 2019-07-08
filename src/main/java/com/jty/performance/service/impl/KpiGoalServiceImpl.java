package com.jty.performance.service.impl;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.Employee;
import com.jty.performance.domain.Goal;
import com.jty.performance.domain.Task;
import com.jty.performance.domain.auth.AuthRole;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.dto.KpiGoalDto;
import com.jty.performance.domain.dto.KpiTaskDto;
import com.jty.performance.domain.form.EmployeeAccForm;
import com.jty.performance.domain.form.GoalForm;
import com.jty.performance.domain.form.TaskForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.DepartmentRepository;
import com.jty.performance.repository.EmployeeRepository;
import com.jty.performance.repository.GoalRepository;
import com.jty.performance.repository.TaskRepository;
import com.jty.performance.repository.auth.UserRepository;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiGoalService;
import com.jty.performance.support.ResultCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * KpiGoalServiceImpl
 *
 * @Author: yeting
 * @Date: 2019/6/27 9:26
 */
@Service
public class KpiGoalServiceImpl implements KpiGoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void addGoal(MyUserDetail user, GoalForm form) {
        Department department = getParentDepartment(user.getDeptId());
        Goal goal = castToDomain(form);
        goal.setDepartment(department);
        goalRepository.save(goal);
    }

    @Override
    public KpiGoalDto getGoalInfoById(Integer goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        return goal.castToDto();
    }

    @Override
    public Page<KpiGoalDto> getGoals(MyUserDetail user, GoalForm form) {
        Department department = checkUserForLeader(user, form);
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(Sort.Order.desc("goalState"));
        sortOrders.add(Sort.Order.desc("goalEstimatedEndTime"));
        sortOrders.add(Sort.Order.desc("goalScore"));
        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize(), Sort.by(sortOrders));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                expressions.add(criteriaBuilder.equal(root.get("goalFlag"), true));
                expressions.add(criteriaBuilder.equal(root.join("department").get("id"), department.getParentId()));
                return predicate;
            }
        };
        Page<Goal> page = goalRepository.findAll(spec, pageable);
        List<Goal> goalList = page.getContent();
        List<KpiGoalDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(goalList)) {
            for (Goal goal : goalList) {
                KpiGoalDto kpigoalDto = goal.castToDto();
                dtos.add(kpigoalDto);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }

    @Override
    public void editGoal(Integer goalId, GoalForm form) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        goal.setGoalName(form.getGoalName());
        goal.setGoalStartTime(form.getGoalStart());
        goal.setGoalEstimatedEndTime(form.getGoalGuessEnd());
        goal.setGoalDescription(form.getDescription());
        goal.getEmployees().clear();
        List<EmployeeAccForm> employeeAcceptList = form.getEmployeeAcceptList();
        if (CollectionUtils.isNotEmpty(employeeAcceptList)) {
            for (EmployeeAccForm employeeAccForm : employeeAcceptList) {
                goal.addEmployee(employeeRepository.findById(employeeAccForm.getAcceptId()).orElse(null));
            }
        }
        goalRepository.save(goal);
    }

    @Override
    public void finishGoal(Integer goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        List<Task> taskByGoal = taskRepository.findByGoalAndTaskFlag(goal, true);
        if (CollectionUtils.isNotEmpty(taskByGoal)) {
            for (Task task : taskByGoal) {
                if (!"5".equals(task.getTaskState() + "")) {
                    throw new BusinessException(ResultCode.GOAL_TASKING);
                }
            }
        }
        goal.setGoalState(0);
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        goal.setGoalEndTime(localDate);
        goalRepository.save(goal);
    }

    @Override
    public void deleteGoal(Integer goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        List<Task> byGoal = taskRepository.findByGoalAndTaskFlag(goal, true);
        if (byGoal.size() > 0) {
            throw new BusinessException(ResultCode.GOAL_HAS_TASK);
        }
        goal.setGoalFlag(false);
        goalRepository.save(goal);
    }

    @Override
    public List<KpiGoalDto> getGoalList(MyUserDetail user, GoalForm form) {
        Department department = checkUserForLeader(user, form);
        Department parentDept = new Department();
        parentDept.setId(department.getParentId());
        List<Goal> allGoalByDept = goalRepository.findByDepartmentAndGoalFlagAndGoalState(parentDept, true, 1);
        ArrayList<KpiGoalDto> kpiGoalDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allGoalByDept)) {
            for (Goal goal : allGoalByDept) {
                KpiGoalDto kpiGoalDto = goal.castToDto();
                kpiGoalDtos.add(kpiGoalDto);
            }
        }
        return kpiGoalDtos;
    }

    private Department checkUserForLeader(MyUserDetail user, GoalForm form) {
        Department department;
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
        if (flag) {
            if (form.getDeptId() != null) {
                department = departmentRepository.findAllByParentId(form.getDeptId()).get(0);
            } else {
                department = departmentRepository.findById(2).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
            }
        } else {
            department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        }
        return department;
    }

    private Goal castToDomain(GoalForm form) {
        Goal goal = new Goal();
        goal.setGoalName(form.getGoalName());
        goal.setGoalStartTime(form.getGoalStart());
        goal.setGoalEstimatedEndTime(form.getGoalGuessEnd());
        goal.setGoalDescription(form.getDescription());
        //1:进行中的任务    0:完成的目标
        goal.setGoalState(1);
        goal.setGoalScore(0);
        goal.setGoalFlag(true);
        List<EmployeeAccForm> employeeAcceptList = form.getEmployeeAcceptList();
        if (CollectionUtils.isNotEmpty(employeeAcceptList)) {
            for (EmployeeAccForm employeeAccForm : employeeAcceptList) {
                goal.addEmployee(employeeRepository.findById(employeeAccForm.getAcceptId()).orElse(null));
            }
        }
        return goal;
    }

    private Department getParentDepartment(Integer deptId) {
        Department department = departmentRepository.findById(deptId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        return departmentRepository.findById(department.getParentId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
    }
}
