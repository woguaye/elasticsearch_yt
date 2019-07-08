package com.jty.performance.service.impl;

import com.jty.performance.domain.*;
import com.jty.performance.domain.auth.AuthRole;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.dto.KpiTaskDto;
import com.jty.performance.domain.dto.KpiTaskNumDto;
import com.jty.performance.domain.dto.KpiTaskTypeDto;
import com.jty.performance.domain.form.EmployeeAccForm;
import com.jty.performance.domain.form.TaskForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.*;
import com.jty.performance.repository.auth.UserRepository;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiTaskService;
import com.jty.performance.support.ResultCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/5/9 21:37
 */
@Service
public class KpiTaskServiceImpl implements KpiTaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PlanCycleRepository planCycleRepository;

    @Autowired
    private LogIntegralRepository logIntegralRepository;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Override
    public void addTask(TaskForm form) {
        AuthUser authUser = new AuthUser();
        authUser.setId(form.getUserId());
        Employee employee = employeeRepository.findByAuthUser(authUser).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department department = departmentRepository.findById(employee.getDepartment().getParentId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Goal goal = goalRepository.findById(form.getGoalId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Task task = new Task();
        task.setTaskName(form.getTaskName());
        task.setTaskStartTime(form.getTaskStart());
        task.setEstimatedEndTime(form.getTaskGuessEnd());
        task.setTaskFlag(true);
        task.setTaskCreator(employee.getEName());
        task.setEmployee(employee);
        task.setDepartment(department);
        task.setGoal(goal);
        task.setDescription(form.getDescription());
        TaskType taskType = taskTypeRepository.findById(form.getTaskTypeId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        task.setTaskType(taskType);
        //添加任务状态，3，任务审核中  0:任务悬赏中
        if ("2".equals(taskType.getId() + "")) {
            task.setTaskState(0);
        } else {
            task.setTaskState(3);
        }
        List<EmployeeAccForm> employeeAcceptList = form.getEmployeeAcceptList();
        if (CollectionUtils.isNotEmpty(employeeAcceptList)) {
            for (EmployeeAccForm employeeAccForm : employeeAcceptList) {
                task.addEmployee(employeeRepository.findById(employeeAccForm.getAcceptId()).orElse(null));
            }
        }
        taskRepository.save(task);
    }

    @Override
    public Page<KpiTaskDto> getTaskApprovalList(MyUserDetail user, TaskForm form) {
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
        List<Sort.Order> sortOrders = new ArrayList<>();
        if ("5".equals(form.getTaskState() + "") || "1".equals(form.getTaskState() + "")) {
            sortOrders.add(Sort.Order.desc("completionTime"));
            sortOrders.add(Sort.Order.desc("modifiedTime"));
        } else {
            sortOrders.add(Sort.Order.asc("estimatedEndTime"));
        }

        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize(), Sort.by(sortOrders));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                expressions.add(criteriaBuilder.equal(root.get("taskState"), form.getTaskState()));
                expressions.add(criteriaBuilder.equal(root.get("taskFlag"), true));
                expressions.add(criteriaBuilder.equal(root.join("department").get("id"), department.getParentId()));
                if (form.getGoalId() != null && !"".equals(form.getGoalId() + "")) {
                    expressions.add(criteriaBuilder.equal(root.join("goal").get("id"), form.getGoalId()));
                }
                Join<Employee, Task> join = root.join("employee", JoinType.LEFT);
                if (!StringUtils.isEmpty(form.getTaskDuty())) {
                    expressions.add(criteriaBuilder.like(join.get("eName"), "%" + form.getTaskDuty() + "%"));
                }
                return predicate;
            }
        };
        Page<Task> page = taskRepository.findAll(spec, pageable);
        List<Task> taskList = page.getContent();
        List<KpiTaskDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (Task task : taskList) {
                KpiTaskDto kpiTaskDto = task.castToDto();
                dtos.add(kpiTaskDto);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void approvalTask(Integer taskId, TaskForm form) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        task.setEstimatedScore(form.getTaskGuessScore());
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        task.setApprovalTime(localDate);
        //任务状态 2：进行中
        task.setTaskState(2);
        taskRepository.save(task);
        Goal goal = task.getGoal();
        goal.setGoalScore(goal.getGoalScore() + task.getEstimatedScore());
        goalRepository.save(goal);
    }

    @Override
    public Page<KpiTaskDto> getTaskManageList(TaskForm form) {
        AuthUser authUser = new AuthUser();
        authUser.setId(form.getUserId());
        Employee employee = employeeRepository.findByAuthUser(authUser).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department department = departmentRepository.findById(form.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(Sort.Order.asc("taskState"));
        sortOrders.add(Sort.Order.desc("modifiedTime"));
        Pageable pageable = PageRequest.of(form.getPageable().getPageNumber(), form.getPageSize(), Sort.by(sortOrders));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                expressions.add(criteriaBuilder.equal(root.get("taskFlag"), true));
                expressions.add(criteriaBuilder.equal(root.join("department").get("id"), department.getParentId()));
                Join<Employee, Task> join = root.join("employee", JoinType.LEFT);
                expressions.add(criteriaBuilder.equal(join.get("id"), employee.getId()));
                return predicate;
            }
        };
        Page<Task> page = taskRepository.findAll(spec, pageable);
        List<Task> taskList = page.getContent();
        List<KpiTaskDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (Task task : taskList) {
                KpiTaskDto kpiTaskDto = task.castToDto();
                dtos.add(kpiTaskDto);
            }
        }
        return new PageImpl<>(dtos, form.getPageable(), page.getTotalElements());
    }

    @Override
    public void applyAcceptTask(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        task.setApplicationAcceptanceTime(localDate);
        //任务状态：4：验收中
        task.setTaskState(4);
        taskRepository.save(task);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void acceptTask(Integer taskId, TaskForm form) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        task.setDegreeCompletion(form.getTaskDegree());
        task.setActualScore(form.getTaskLastScore());
        //任务状态：1：待分配状态
        task.setTaskState(1);
        taskRepository.save(task);
        Goal goal = task.getGoal();
        goal.setGoalScore(goal.getGoalScore() - task.getEstimatedScore() + task.getActualScore());
        goalRepository.save(goal);
        //修改本考核期使用积分已使用积分
//        PlanCycle planCycle = planCycleRepository.findByPlanCycleState(1).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
//        planCycle.setPlanCycleUseScore(planCycle.getPlanCycleUseScore() + form.getTaskLastScore());
//        planCycleRepository.save(planCycle);
        //任务创建人获得积分
//        Employee employee = employeeRepository.findById(task.getEmployee().getId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
//        employee.setCurrentScore(employee.getCurrentScore() + form.getTaskLastScore());
//        employeeRepository.save(employee);
//        //添加积分历史变动
//        LogIntegral logIntegral = new LogIntegral();
//        logIntegral.setEmployee(employee);
//        logIntegral.setIntegModifyNum(form.getTaskLastScore());
//        logIntegral.setIntegModifyReason("完成任务：" + task.getTaskName());
//        IntegralModifyType integralModifyType = new IntegralModifyType();
//        integralModifyType.setId(1);
//        logIntegral.setIntegralModifyType(integralModifyType);
//        logIntegral.setPlanCycle(planCycle);
//        logIntegralRepository.save(logIntegral);
    }

    /**
     * 2:进行中，3:审核中 4:验收中 5:已完成
     *
     * @return
     */
    @Override
    public KpiTaskNumDto getTaskNumByState(Integer deptId) {
        Department department = departmentRepository.findById(deptId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        KpiTaskNumDto kpiTaskNumDto = new KpiTaskNumDto();
        Integer taskApprovalNum = taskRepository.findCountByTaskState(3, department.getParentId());
        Integer taskAcceptNum = taskRepository.findCountByTaskState(4, department.getParentId());
        kpiTaskNumDto.setTaskApprovalNum(taskApprovalNum);
        kpiTaskNumDto.setTaskAcceptNum(taskAcceptNum);
        return kpiTaskNumDto;
    }

    @Override
    public void deleteTask(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        task.setTaskFlag(false);
        taskRepository.save(task);
    }

    @Override
    public void rejectTask(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        task.setTaskState(2);
        taskRepository.save(task);
    }

    @Override
    public List<KpiTaskTypeDto> getTaskTypes() {
        ArrayList<KpiTaskTypeDto> kpiTaskTypeDtos = new ArrayList<>();
        List<TaskType> allTaskType = taskTypeRepository.findAll();
        if (CollectionUtils.isNotEmpty(allTaskType)) {
            for (TaskType taskType : allTaskType) {
                KpiTaskTypeDto dto = taskType.castToDto();
                kpiTaskTypeDtos.add(dto);
            }
        }
        return kpiTaskTypeDtos;
    }

    @Override
    public KpiTaskNumDto getAllotTaskNumByState(Integer userId) {
        KpiTaskNumDto kpiTaskNumDto = new KpiTaskNumDto();
        Integer count = taskRepository.findCountByUserTaskState(1, userId);
        kpiTaskNumDto.setTaskAllotNum(count);
        return kpiTaskNumDto;
    }

    @Override
    public KpiTaskDto getTaskInfoById(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        KpiTaskDto kpiTaskDto = task.castToDto();
        return kpiTaskDto;
    }

    @Override
    public void rewardTask(Integer taskId, TaskForm form) {
        synchronized (taskId) {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
            if (!"0".equals(task.getTaskState() + "")) {
                throw new BusinessException(ResultCode.REWARD_HAS_GET);
            }
            AuthUser authUser = new AuthUser();
            authUser.setId(form.getUserId());
            Employee employee = employeeRepository.findByAuthUser(authUser).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
            //修改负责人信息
            task.setEmployee(employee);
            task.setTaskStartTime(form.getTaskStart());
            task.setEstimatedEndTime(form.getTaskGuessEnd());
            task.setDescription(form.getDescription());
            task.setTaskState(3);
            List<EmployeeAccForm> employeeAcceptList = form.getEmployeeAcceptList();
            if (CollectionUtils.isNotEmpty(employeeAcceptList)) {
                task.getEmployees().clear();
                for (EmployeeAccForm employeeAccForm : employeeAcceptList) {
                    task.addEmployee(employeeRepository.findById(employeeAccForm.getAcceptId()).orElse(null));
                }
            }
            taskRepository.save(task);
        }

    }
}
