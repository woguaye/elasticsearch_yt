package com.jty.performance.domain;

import lombok.Data;

import javax.persistence.Entity;

/**
 * @Author: yeting
 * @Date: 2019/5/7 14:41
 */
@Data
@Entity
public class Parameter extends SuperEntity {

    /**
     * 默认积分100积分 人/天
     */
    private Integer scoreArgument;


    /**
     * 签到积分20积分 每天
     */
    private Integer scoreSign;

}
