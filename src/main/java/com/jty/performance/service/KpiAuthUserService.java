package com.jty.performance.service;

import com.jty.performance.domain.form.auth.PasswordForm;
import com.jty.performance.security.MyUserDetail;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Author: yeting
 * @Date: 2019/5/15 11:22
 */
public interface KpiAuthUserService {

    UserDetails getUserInfo(MyUserDetail user);

    void updatePassword(Integer userId, PasswordForm passwordForm);
}
