package com.jty.performance.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * KpiGoalDto
 *
 * @Author: yeting
 * @Date: 2019/6/28 9:55
 */
@Data
public class KpiGoalDto extends SuperDto {

    /**
     * 目标名称
     */
    private String goalName;

    /**
     * 目标ID
     */
    private Integer goalId;

    /**
     * 目标开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate goalStart;

    /**
     * 目标结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate goalGuessEnd;

    /**
     * 目标结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate goalEnd;

    /**
     * 目标使用积分
     */
    private Integer goalScore;

    /**
     * 目标描述
     */
    private String description;

    /**
     * 负责人
     */
    private List<KpiGoalEmplDto> employeeList;

    /**
     * 目标状态
     * true:进行中的任务
     * false:完成的目标
     */
    private Boolean goalState;
}
