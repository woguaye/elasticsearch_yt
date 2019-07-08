package com.jty.performance.domain.dto;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/4/29 8:30
 */
@Data
public class KpiDeptIntegralDto extends SuperDto {

    /**
     * 部门绩效统计时间
     */
    private String time;

    /**
     * 部门绩效总收入
     */
    private Integer IntegralRecord;
}
