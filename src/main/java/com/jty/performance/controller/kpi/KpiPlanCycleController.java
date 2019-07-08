package com.jty.performance.controller.kpi;

import com.jty.performance.annotation.MyLog;
import com.jty.performance.domain.form.PlanCycleForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiPlanCycleService;
import com.jty.performance.support.Result;
import com.jty.performance.support.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: yeting
 * @Date: 2019/5/7 15:06
 */
@RestController
@RequestMapping(value = "/kpi")
public class KpiPlanCycleController {


    @Autowired
    private KpiPlanCycleService kpiPlanCycleService;

    /**
     * 获取当前考核期详情
     *
     * @return
     */
    @GetMapping("/plancycle/current")
    public Result getPlanCycleInfo(@AuthenticationPrincipal MyUserDetail user) {
        return Result.success(kpiPlanCycleService.getPlanCycleInfo(user));
    }


    /**
     * 修改考核期结束时间
     *
     * @param planCycleId
     * @param form
     * @return
     */
    @MyLog(value = "编辑考核期时间")
    @PutMapping("/plancycle/edit/{planCycleId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_PLANCYCLE')")
    public Result updatePlanCycleTime(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer planCycleId, @RequestBody PlanCycleForm form) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiPlanCycleService.updatePlanCycleTime(planCycleId, form);
        return Result.success();
    }

    /**
     * 当前考核期积分预算
     *
     * @param planCycleId
     * @return
     */
    @GetMapping("/integral/budget/{planCycleId}")
    public Result getBudgetScore(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer planCycleId) {
        return Result.success(kpiPlanCycleService.getBudgetScore(user, planCycleId));
    }


    /**
     * 获取历史考核期
     *
     * @return
     */
    @GetMapping("/plancycle/plancycle-past")
    public Result getPlanCyclePast(@AuthenticationPrincipal MyUserDetail user) {
        return Result.success(kpiPlanCycleService.getPlanCyclePast(user));
    }


    /**
     * 根据ID获取考核期详情
     *
     * @return
     */
    @GetMapping("/plancycle/plancycle-info")
    public Result getPlanCycleInfoById(@AuthenticationPrincipal MyUserDetail user, PlanCycleForm form) {
        Integer planCycleId = form.getPlanCycleId();
        return Result.success(kpiPlanCycleService.getPlanCycleInfoById(user, planCycleId));
    }


    /**
     * 根据部门ID获取历史考核期
     *
     * @param deptId
     * @return
     */
    @GetMapping("/plancycle/plancycle-dept/{deptId}")
    public Result getPlanCycleByDeptId(@PathVariable Integer deptId) {
        return Result.success(kpiPlanCycleService.getPlanCycleByDeptId(deptId));
    }

}
