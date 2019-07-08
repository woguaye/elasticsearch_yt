package com.jty.performance.domain.form;

import lombok.Data;

import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/5/11 16:03
 */
@Data
public class AcceptEmployeeForm {

    /**
     * 接收积分用户信息集合
     */
    private List<EmployeeAccForm> employeeAcceptList;
}
