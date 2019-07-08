package com.jty.performance.controller.auth;

import com.jty.performance.annotation.MyLog;
import com.jty.performance.domain.dto.auth.SRBRoleDto;
import com.jty.performance.domain.form.auth.AuthRoleForm;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.auth.KPIRoleService;
import com.jty.performance.support.Result;
import com.jty.performance.util.PoCastUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: yeting
 * @Date: 2019/6/14 15:08
 */
@RestController
@RequestMapping(value = "/kpi")
public class KPIRoleController {

    @Autowired
    private KPIRoleService roleService;

    /**
     * 角色列表
     *
     * @param form
     * @return
     */
    @GetMapping("/auth/manage/roles")
    public Result getRoles(@AuthenticationPrincipal MyUserDetail user, @Valid AuthRoleForm form) {
        return Result.success(PoCastUtils.poPageCastToDto(roleService.findPageRolesBySystem(form), SRBRoleDto.class));
    }


    /**
     * 新增角色
     *
     * @param form
     * @return
     */
    @MyLog(value = "新增角色")
    @PostMapping("/auth/manage/roles")
    @PreAuthorize("hasAuthority('KPI_MANAGE_ROLE')")
    public Result addRole(@RequestBody @Valid AuthRoleForm form) {
        roleService.addRole(form);
        return Result.success();
    }

    /**
     * 设置角色权限
     *
     * @param roleId
     * @param form
     * @return
     */
    @MyLog(value = "设置权限")
    @PutMapping("/auth/manage/roles/{roleId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_ROLE')")
    public Result updateRoleAuthority(@PathVariable Integer roleId, @RequestBody @Valid AuthRoleForm form) {
        roleService.updateRole(roleId, form);
        return Result.success();
    }


    /**
     * 获取角色详情
     *
     * @param roleId
     * @return
     */
    @GetMapping("/auth/manage/roles/{roleId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_ROLE')")
    public Result findAuthDetail(@PathVariable Integer roleId) {
        return Result.success(roleService.getRoleById(roleId).cashToDto());
    }

}
