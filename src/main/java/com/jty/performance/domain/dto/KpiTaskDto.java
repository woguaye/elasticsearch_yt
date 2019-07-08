package com.jty.performance.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/5/10 8:58
 */
@Data
public class KpiTaskDto extends SuperDto {

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务创建人
     */
    private String taskCreator;

    /**
     * 任务责任人
     */
    private String taskDuty;

    /**
     * 任务开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate taskStart;

    /**
     * 任务预计结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate taskGuessEnd;

    /**
     * 任务实际完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate taskEnd;

    /**
     * 任务完成度
     */
    private Integer taskDegree;

    /**
     * 任务状态，
     * 0: 悬赏中
     * 1：分配中
     * 2: 审批中
     * 3：进行中
     * 4：验收中
     * 5：已完成
     */
    private Integer taskState;

    /**
     * 任务预计积分
     */
    private Integer taskGuessScore;

    /**
     * 任务实际积分
     */
    private Integer taskScore;

    /**
     * 目标ID
     */
    private Integer goalId;

    /**
     * 目标名称
     */
    private String goalName;

    /**
     * 任务类型ID
     */
    private Integer taskTypeId;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 人员结构
     */
    private List<KpiTaskEmplDto> employeeList;
}
