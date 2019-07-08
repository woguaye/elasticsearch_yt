package com.jty.performance.controller.kpi;

import com.jty.performance.annotation.MyLog;
import com.jty.performance.domain.form.DepartmentForm;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.DepartmentService;
import com.jty.performance.support.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * KpiDepartmentController
 *
 * @Author: yeting
 * @Date: 2019/6/16 21:47
 */
@RestController
@RequestMapping(value = "/kpi")
public class KpiDepartmentController {

    @Autowired
    private DepartmentService departmentService;


    /**
     * 创建中心部门
     *
     * @param form
     * @return
     */
    @MyLog(value = "创建中心部门")
    @PostMapping("/auth/manage/department-core")
    @PreAuthorize("hasAuthority('KPI_MANAGE_DEPARTMENT')")
    public Result createDepartmentCore(@RequestBody @Valid DepartmentForm form) {
        return Result.success(departmentService.addDepartmentCore(form));
    }

    /**
     * 创建中心子部门
     *
     * @param deptId
     * @param form
     * @return
     */
    @MyLog(value = "创建中心子部门")
    @PostMapping("/auth/manage/department/{deptId}")
    public Result createDepartmentChild(@PathVariable Integer deptId, @RequestBody @Valid DepartmentForm form) {
        return Result.success(departmentService.addDepartmentChild(deptId, form));
    }

    /**
     * 部门下拉选
     *
     * @param user
     * @return
     */
    @GetMapping("/manage/departments/dropdown")
    public Result getDepartmentsByAuth(@AuthenticationPrincipal MyUserDetail user) {
        return Result.success(departmentService.getDepartmentsByAuth(user));
    }

    /**
     * 修改部门
     *
     * @param deptId
     * @param form
     * @return
     */
    @MyLog(value = "编辑部门信息")
    @PutMapping("/auth/manage/department/{deptId}/alter")
    @PreAuthorize("hasAuthority('KPI_MANAGE_DEPARTMENT')")
    public Result alterDepartment(@PathVariable Integer deptId, @RequestBody @Valid DepartmentForm form) {
        return Result.success(departmentService.alterDepartment(deptId, form));
    }

    /**
     * 获取中心部门结构
     *
     * @return
     */
    @GetMapping("/manage/departments/cores")
    public Result getDepartmentCores() {
        return Result.success(departmentService.getDepartmentCores());
    }

}
