package com.jty.performance.controller.auth;

import com.jty.performance.annotation.MyLog;
import com.jty.performance.domain.form.auth.KPIManagerHistoryForm;
import com.jty.performance.domain.form.auth.KPIManagerSearchForm;
import com.jty.performance.domain.form.auth.KPIStaffForm;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.auth.KPIManagerService;
import com.jty.performance.support.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * AuthUserManagerController
 *
 * @Author: yeting
 * @Date: 2019/5/22 14:09
 */
@RestController
@RequestMapping(value = "/kpi")
public class KPIManagerController {

    @Autowired
    private KPIManagerService kpiManagerService;


    /**
     * 获取后台用户列表
     *
     * @return Result
     */
    @GetMapping("/auth/manage/staffs")
    @PreAuthorize("hasAuthority('KPI_MANAGE_STAFF')")
    public Result getManagers(@AuthenticationPrincipal MyUserDetail user, @Valid KPIManagerSearchForm form) {
        return Result.success(kpiManagerService.getManagers(user, form));
    }


    /**
     * 新增用户
     *
     * @param form
     * @return
     */
    @MyLog(value = "新增用户")
    @PostMapping("/auth/manage/staff")
    @PreAuthorize("hasAuthority('KPI_MANAGE_STAFF')")
    public Result addStaff(@RequestBody @Valid KPIStaffForm form) {
        kpiManagerService.addStaff(form);
        return Result.success();
    }

    /**
     * 更新用户信息
     *
     * @param staffId
     * @param form
     * @return
     */
    @MyLog(value = "编辑用户")
    @PutMapping("/auth/manage/staff/{staffId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_STAFF')")
    public Result updateStaff(@PathVariable Integer staffId, @RequestBody @Valid KPIStaffForm form) {
        form.setUserId(staffId);
        kpiManagerService.updateStaff(form);
        return Result.success();
    }

    /**
     * 重置密码
     *
     * @param staffId
     * @return
     */
    @MyLog(value = "重置密码")
    @PostMapping("/auth/manage/staff/{staffId}/password/reset")
    @PreAuthorize("hasAuthority('KPI_MANAGE_STAFF')")
    public Result resetPassword(@PathVariable Integer staffId) {
        kpiManagerService.resetPassword(staffId);
        return Result.success();
    }


    /**
     * 启用用户
     *
     * @param staffId
     * @param operator
     * @param form
     * @return
     */
    @MyLog(value = "用户启用")
    @PutMapping("/auth/manage/staff/{staffId}/enable")
    @PreAuthorize("hasAuthority('KPI_MANAGE_STAFF')")
    public Result enableManager(@PathVariable Integer staffId, @AuthenticationPrincipal MyUserDetail operator, @RequestBody KPIManagerHistoryForm form) {
        kpiManagerService.enableManager(staffId, operator.getUserId(), form.getOperateReason());
        return Result.success();
    }

    /**
     * 禁用用户
     *
     * @param staffId
     * @param operator
     * @param form
     * @return
     */
    @MyLog(value = "用户禁用")
    @PutMapping("/auth/manage/staff/{staffId}/disable")
    @PreAuthorize("hasAuthority('KPI_MANAGE_STAFF')")
    public Result disableManager(@PathVariable Integer staffId, @AuthenticationPrincipal MyUserDetail operator, @RequestBody KPIManagerHistoryForm form) {
        kpiManagerService.disableManager(staffId, operator.getUserId(), form.getOperateReason());
        return Result.success();
    }


    /**
     * 获取用户操作历史
     *
     * @param staffId
     * @param form
     * @return
     */
    @GetMapping("/auth/manage/history/staff/{staffId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_STAFF')")
    public Result getOperateHistory(@PathVariable Integer staffId, KPIManagerHistoryForm form) {
        return Result.success(kpiManagerService.findHistoryByManagerId(staffId, form));
    }


    /**
     * 删除用户
     *
     * @param staffId 员工ID
     * @return
     */
    @MyLog(value = "删除用户")
    @DeleteMapping("/auth/manage/staff/{staffId}/delete")
    @PreAuthorize("hasAuthority('KPI_MANAGE_STAFF')")
    public Result deleteStaff(@PathVariable Integer staffId) {
        kpiManagerService.deleteStaff(staffId);
        return Result.success();
    }

}
