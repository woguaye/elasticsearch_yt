package com.jty.performance.domain;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 积分变动类型表
 * @Author: yeting
 * @Date: 2019/4/25 16:55
 */
@Data
@Entity
@Table(name = "integral_miodify_type")
@Proxy(lazy = false)
public class IntegralModifyType extends SuperEntity {

    /**
     * 积分变动类型
     */
    private String modifyType;

}
