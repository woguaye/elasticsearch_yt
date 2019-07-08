package com.jty.performance.domain.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/5/9 21:16
 */
@Data
public class TaskForm extends PageableForm {

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务开始时间
     */
    private LocalDate taskStart;

    /**
     * 任务预计结束时间
     */
    private LocalDate taskGuessEnd;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 任务状态
     */
    private Integer taskState;

    /**
     * 任务预计积分
     */
    private Integer taskGuessScore;


    /**
     * 任务完成度
     */
    private Integer taskDegree;

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 任务实际积分
     */
    private Integer taskLastScore;

    /**
     * 任务类型ID
     */
    private Integer taskTypeId;

    /**
     * 任务描述
     */
    @Length(max = 200, message = "内容不能超过200个字符")
    private String description;

    /**
     * 任务的人员结构
     */
    private List<EmployeeAccForm> employeeAcceptList;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 目标ID
     */
    private Integer goalId;

    /**
     * 责任人名称
     */
    private String taskDuty;
}
