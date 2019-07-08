package com.jty.performance.domain.form.auth;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * PasswordForm
 * @Author: yeting
 * @Date: 2019/5/14 22:00
 */
@Data
public class PasswordForm {

    @Pattern(regexp="^(\\w){6,24}$",message = "密码格式不对")
    private String oldPassword;

    @Pattern(regexp="^(\\w){6,24}$",message = "密码格式不对")
    private String newPassword;
}
