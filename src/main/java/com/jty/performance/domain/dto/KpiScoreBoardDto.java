package com.jty.performance.domain.dto;

import lombok.Data;

/**
 * 积分排行榜Dto
 *
 * @Author: yeting
 * @Date: 2019/4/23 22:52
 */
@Data
public class KpiScoreBoardDto extends SuperDto {

    /**
     * 员工ID
     */
    private Integer userId;

    /**
     * 本季度排名
     */
    private Integer rank;

    /**
     * 员工姓名
     */
    private String userName;

    /**
     *  员工职称
     */
    private String position;

    /**
     * 本季度积分
     */
    private Integer quarterIntegra;

    /**
     * 上季度派排名
     */
    private Integer preRank;

    /**
     * 上季度积分
     */
    private Integer preQuarterIntegra;

    /**
     * 资历
     */
    private Integer qualification;



}
