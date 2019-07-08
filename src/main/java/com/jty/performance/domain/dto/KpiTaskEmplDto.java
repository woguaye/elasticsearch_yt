package com.jty.performance.domain.dto;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/5/24 8:36
 */
@Data
public class KpiTaskEmplDto {

    /**
     * 参与人员名称
     */
    private String emplName;

    /**
     * 人员所得积分
     */
    private Integer acceptScore;

    /**
     * 参与人员ID
     */
    private Integer empId;
}
