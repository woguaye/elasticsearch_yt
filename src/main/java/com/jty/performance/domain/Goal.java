package com.jty.performance.domain;

import com.jty.performance.domain.dto.KpiGoalDto;
import com.jty.performance.domain.dto.KpiGoalEmplDto;
import com.jty.performance.support.DomainRegistry;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal
 *
 * @Author: yeting
 * @Date: 2019/6/27 8:42
 */
@Data
@Entity
@Table(name = "goal")
public class Goal extends SuperEntity {

    /**
     * 目标名称
     */
    private String goalName;

    /**
     * 目标开始时间
     */
    private LocalDate goalStartTime;

    /**
     * 目标预计结束时间
     */
    private LocalDate goalEstimatedEndTime;

    /**
     * 目标完成时间
     */
    private LocalDate goalEndTime;

    /**
     * 目标描述
     */
    private String goalDescription;

    /**
     * 责任人
     */
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "kpi_relation_goal_empl",
            joinColumns = {@JoinColumn(name = "goal_id")},
            inverseJoinColumns = {@JoinColumn(name = "empl_id")})
    private List<Employee> employees = new ArrayList<>();

    /**
     * 部门信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Department department;

    /**
     * 目标状态
     * 1:进行中的任务
     * 0:完成的目标
     */
    private Integer goalState;

    /**
     * 目标标记
     * true:正常目标
     * false:目标已删除
     */
    private Boolean goalFlag;

    /**
     * 目标当前使用积分
     */
    private Integer goalScore;


    public void addEmployee(Employee employee) {
        if (employee == null) return;
        employees.add(employee);
    }

    public KpiGoalDto castToDto() {
        KpiGoalDto goalDto = new KpiGoalDto();
        goalDto.setGoalName(this.goalName);
        goalDto.setGoalId(this.getId());
        goalDto.setGoalStart(this.getGoalStartTime());
        goalDto.setGoalGuessEnd(this.getGoalEstimatedEndTime());
        goalDto.setGoalEnd(this.getGoalEndTime());
        goalDto.setDescription(this.getGoalDescription());
        goalDto.setGoalScore(this.getGoalScore());
        goalDto.setGoalState(this.goalState == 1 ? true : false);
        ArrayList<KpiGoalEmplDto> kpiGoalEmplDtos = new ArrayList<>();
        List<Employee> employees = this.getEmployees();
        if (CollectionUtils.isNotEmpty(employees)) {
            for (Employee employee : employees) {
                KpiGoalEmplDto kpiGoalEmplDto = new KpiGoalEmplDto();
                kpiGoalEmplDto.setEmplName(employee.getEName());
                kpiGoalEmplDto.setEmpId(employee.getId());
                kpiGoalEmplDtos.add(kpiGoalEmplDto);
            }
        }
        goalDto.setEmployeeList(kpiGoalEmplDtos);
        return goalDto;

    }
}
