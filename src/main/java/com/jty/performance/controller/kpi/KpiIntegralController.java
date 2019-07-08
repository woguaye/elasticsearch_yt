package com.jty.performance.controller.kpi;

import com.jty.performance.annotation.MyLog;
import com.jty.performance.domain.form.AcceptEmployeeForm;
import com.jty.performance.domain.form.KpiScoreBoardForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiIntegralService;
import com.jty.performance.support.Result;
import com.jty.performance.support.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: yeting
 * @Date: 2019/4/28 22:19
 */
@RestController
@RequestMapping(value = "/kpi")
public class KpiIntegralController {

    @Autowired
    private KpiIntegralService kpiIntegralService;

    /**
     * 个人积分变动情况
     *
     * @param form
     * @return
     */
    @GetMapping("/integral/{userId}")
    public Result findEmployeeIntegralInfo(KpiScoreBoardForm form) {
        return Result.success(kpiIntegralService.findIntegralInfo(form));
    }

    /**
     * 个人历史贡献值变动情况
     *
     * @param form
     * @return
     */
    @GetMapping("/integralRecord/{userId}")
    public Result findEmployeeRecord(KpiScoreBoardForm form) {
        return Result.success(kpiIntegralService.findRecordInfo(form));
    }


    /**
     * 部门人员树形菜单
     *
     * @param
     * @return
     */
    @GetMapping("/dividescore/employee_nodes")
    public Result findEmployeeNodes(@AuthenticationPrincipal MyUserDetail user) {
        return Result.success(kpiIntegralService.findEmployeeNodes(user));
    }


    /**
     * 个人积分划分
     *
     * @param eId
     * @param acceptEmployee
     * @return
     */
    @MyLog(value = "个人积分划分")
    @PutMapping("/dividescore/{eId}")
    public Result divideScoreToEmployee(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer eId, @RequestBody AcceptEmployeeForm acceptEmployee) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiIntegralService.divideScoreToEmployee(user, eId, acceptEmployee);
        return Result.success();
    }


    /**
     * 任务积分划分
     *
     * @param taskId
     * @param acceptEmployee
     * @return
     */
    @MyLog(value = "任务积分划分")
    @PutMapping("/task/dividescore/{taskId}")
    public Result divideTaskScoreToEmployee(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer taskId, @RequestBody AcceptEmployeeForm acceptEmployee) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiIntegralService.divideTaskScoreToEmployee(user, taskId, acceptEmployee);
        return Result.success();
    }

    /**
     * 用户签到
     *
     * @param user
     * @return
     */
    @MyLog(value = "用户签到")
    @PostMapping("/integral/sign")
    @PreAuthorize("hasAuthority('KPI_STAFF_SIGN')")
    public Result staffSign(@AuthenticationPrincipal UserDetails user) {
        MyUserDetail myUserDetail = (MyUserDetail) user;
        //设置浏览账号
        if ("41".equals(myUserDetail.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        return Result.success(kpiIntegralService.staffSign(myUserDetail.getUserId()));
    }

    /**
     * 用户签到积分列表
     *
     * @param form
     * @return
     */
    @GetMapping("/integral-sign/{userId}")
    public Result findEmployeeSign(KpiScoreBoardForm form) {
        return Result.success(kpiIntegralService.findEmployeeSign(form));
    }
}
