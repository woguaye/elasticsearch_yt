package com.jty.performance.domain.form;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/6/22 11:21
 */
@Data
public class EmployeeForm {
    private String eName;

    /**
     * 职位名称
     */
    private String ePosition;


    /**
     * 本考核期积分
     */
    private Integer currentScore;


    /**
     * 员工编码
     */
    private String staffCode;
}
