package com.jty.performance.controller.auth;

import com.jty.performance.annotation.MyLog;
import com.jty.performance.domain.form.auth.PasswordForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiAuthUserService;
import com.jty.performance.support.Result;
import com.jty.performance.support.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: yeting
 * @Date: 2019/5/14 22:00
 */
@RestController
@RequestMapping(value = "/kpi")
public class AuthUserController {

    @Autowired
    private KpiAuthUserService kpiAuthUserService;

    /**
     * 获取用户信息
     *
     * @param user
     * @return
     */
    @GetMapping("/auth/me")
    public Result getCurrentUser(@AuthenticationPrincipal MyUserDetail user) {
        return Result.success(kpiAuthUserService.getUserInfo(user));
    }

    /**
     * 修改密码
     *
     * @param user
     * @param passwordForm
     * @return
     */
    @MyLog(value = "修改密码")
    @PostMapping("/auth/reset/password")
    public Result changePassword(@AuthenticationPrincipal MyUserDetail user, @RequestBody PasswordForm passwordForm) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiAuthUserService.updatePassword(user.getUserId(), passwordForm);
        return Result.success();
    }

}
