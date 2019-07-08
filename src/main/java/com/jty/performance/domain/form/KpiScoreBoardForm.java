package com.jty.performance.domain.form;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/4/26 15:49
 */
@Data
public class KpiScoreBoardForm extends PageableForm {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 部门id
     */
    private Integer deptId;


    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序规则
     */
    private String direction;

    /**
     * 考核期ID
     */
    private Integer planCycleId;


    /**
     * 积分变动类型
     */
    private Integer scoreTypeId;



}
