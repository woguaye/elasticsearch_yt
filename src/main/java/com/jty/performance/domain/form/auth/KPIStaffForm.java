package com.jty.performance.domain.form.auth;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.Set;

/**
 * KPIStaffForm
 *
 * @author yeting
 * @since 2019/1/17 10:48
 */
@Data
public class KPIStaffForm {

    private Integer userId;

    /**
     * 员工工号
     */
    private String account;

    /**
     * 职位
     */
    private String position;

    /**
     * 密码
     */
    private String password;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 员工姓名
     */
    private String realName;

    /**
     * 角色ID集合
     */
    private Set<Integer> roleIds;
}
