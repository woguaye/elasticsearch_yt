package com.jty.performance.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author: yeting
 * @Date: 2019/5/7 15:20
 */
@Data
public class KpiPlanCycleDto extends SuperDto {

    /**
     * 考核期ID
     */
    private Integer planCycleId;

    /**
     * 考核期名称
     */
    private String planCycleName;

    /**
     * 考核期开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planCycleStart;

    /**
     * 考核期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planCycleEnd;

    /**
     * 当前考核期内发放的总积分数
     */
    private Integer planCycleScore;

    /**
     * 考核期预计总积分
     */
    private Integer planCycleEstimateScore;

    /**
     * 当前考核期积分总预算
     */
    private Integer currentBudget;

    /**
     * 考核期数
     */
    private Integer count;

    /**
     * 部门ID
     */
    private Integer deptId;
}
