package com.jty.performance.domain.form;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/5/11 16:33
 */
@Data
public class EmployeeAccForm {

    /**
     * 接收积分ID
     */
    private Integer acceptId;

    /**
     * 接收积分数
     */
    private Integer acceptScore;

    /**
     * 接收原因
     */
    private String acceptReason;
}
