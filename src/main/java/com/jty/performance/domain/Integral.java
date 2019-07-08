package com.jty.performance.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 积分表
 * @Author: yeting
 * @Date: 2019/4/27 22:16
 */
@Data
@Entity
@Table(name = "integral")
public class Integral extends SuperEntity {

    /**
     * 季度
     */
    private Integer quarter;

    /**
     * 员工id
     */
    @Column(name="e_id")
    private Integer eId;

    /**
     * 积分值
     */
    @Column(name="score")
    private Integer score;

}
