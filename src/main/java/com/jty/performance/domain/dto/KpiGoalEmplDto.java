package com.jty.performance.domain.dto;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/6/28 10:29
 */
@Data
public class KpiGoalEmplDto extends SuperDto {
    /**
     * 负责人员名称
     */
    private String emplName;


    /**
     * 负责人员ID
     */
    private Integer empId;
}
