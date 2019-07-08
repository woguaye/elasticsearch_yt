package com.jty.performance.domain;

import com.jty.performance.domain.dto.KpiTaskDto;
import com.jty.performance.domain.dto.KpiTaskEmplDto;
import com.jty.performance.repository.LogIntegralRepository;
import com.jty.performance.support.DomainRegistry;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/5/7 14:08
 */
@Data
@Entity
@Table(name = "task")
public class Task extends SuperEntity {

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 责任人id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Employee employee;

    /**
     * 任务创建时间，任务提交时间
     */
    @Column(name = "task_created_time", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间'")
    private LocalDate taskCreatedTime;

    /**
     * 任务开始时间
     */
    private LocalDate taskStartTime;

    /**
     * 任务预计结束时间
     */
    private LocalDate estimatedEndTime;

    /**
     * 审批通过时间
     */
    private LocalDate approvalTime;

    /**
     * 申请验收时间
     */
    private LocalDate applicationAcceptanceTime;

    /**
     * 完成时间
     */
    private LocalDate completionTime;

    /**
     * 预期积分
     */
    private Integer estimatedScore;

    /**
     * 任务完成度
     */
    private Integer degreeCompletion;

    /**
     * 实际积分
     */
    private Integer actualScore;

    /**
     * 任务状态
     * 0；悬赏中
     * 1：分配中
     * 2: 进行中
     * 3：审批中
     * 4：验收中
     * 5：已完成
     */
    private Integer taskState;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务类型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "task_type_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private TaskType taskType;

    /**
     * 部门信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Department department;

    /**
     * 考核期信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "goal_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Goal goal;

    /**
     * 任务创建人
     */
    private String taskCreator;

    /**
     * 任务人员结构
     */
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "kpi_relation_task_empl",
            joinColumns = {@JoinColumn(name = "task_id")},
            inverseJoinColumns = {@JoinColumn(name = "empl_id")})
    private List<Employee> employees = new ArrayList<>();

    /**
     * 任务标记
     */
    private Boolean taskFlag;

    public KpiTaskDto castToDto() {
        KpiTaskDto kpiTaskDto = new KpiTaskDto();
        kpiTaskDto.setTaskId(this.getId());
        kpiTaskDto.setTaskName(this.taskName);
        kpiTaskDto.setTaskCreator(this.taskCreator);
        kpiTaskDto.setTaskDuty(this.employee.getEName());
        kpiTaskDto.setTaskStart(this.taskStartTime);
        kpiTaskDto.setTaskGuessEnd(this.estimatedEndTime);
        kpiTaskDto.setTaskState(this.taskState);
        kpiTaskDto.setTaskGuessScore(this.estimatedScore);
        kpiTaskDto.setTaskEnd(this.completionTime);
        kpiTaskDto.setTaskDegree(this.degreeCompletion);
        kpiTaskDto.setTaskScore(this.actualScore);
        kpiTaskDto.setGoalId(this.goal.getId());
        kpiTaskDto.setGoalName(this.goal.getGoalName());
        kpiTaskDto.setTaskTypeId(this.taskType.getId());
        kpiTaskDto.setDescription(this.description);
        ArrayList<KpiTaskEmplDto> kpiTaskEmplDtos = new ArrayList<>();
        List<Employee> employees = this.getEmployees();
        if (CollectionUtils.isNotEmpty(employees)) {
            for (Employee employee : employees) {
                KpiTaskEmplDto kpiTaskEmplDto = new KpiTaskEmplDto();
                kpiTaskEmplDto.setEmplName(employee.getEName());
                kpiTaskEmplDto.setEmpId(employee.getId());
                LogIntegral logIntegral = DomainRegistry.logIntegralRepository().findByEmployeeAndTask(employee, this).orElse(null);
                if (logIntegral != null) {
                    kpiTaskEmplDto.setAcceptScore(logIntegral.getIntegModifyNum());
                }
                kpiTaskEmplDtos.add(kpiTaskEmplDto);
            }
        }
        kpiTaskDto.setEmployeeList(kpiTaskEmplDtos);
        return kpiTaskDto;
    }

    public void addEmployee(Employee employee) {
        if (employee == null) return;
        employees.add(employee);
    }
}
