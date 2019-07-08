package com.jty.performance.controller.kpi;

import com.jty.performance.annotation.MyLog;
import com.jty.performance.domain.form.GoalForm;
import com.jty.performance.domain.form.TaskForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiGoalService;
import com.jty.performance.support.Result;
import com.jty.performance.support.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: yeting
 * @Date: 2019/6/27 9:24
 */
@RestController
@RequestMapping(value = "/kpi")
public class KpiGoalController {

    @Autowired
    private KpiGoalService kpiGoalService;

    @MyLog(value = "目标创建")
    @PostMapping("/goal/add")
    @PreAuthorize("hasAuthority('KPI_MANAGE_GOAL')")
    public Result createTask(@AuthenticationPrincipal MyUserDetail user, @RequestBody @Valid GoalForm form) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiGoalService.addGoal(user, form);
        return Result.success();
    }

    /**
     * 获取目标详情
     *
     * @param goalId
     * @return
     */
    @GetMapping("/goal/{goalId}")
    public Result getGoalInfoById(@PathVariable Integer goalId) {
        return Result.success(kpiGoalService.getGoalInfoById(goalId));
    }


    /**
     * 目标公示
     *
     * @param user
     * @param form
     * @return
     */
    @GetMapping("/goals")
    @PreAuthorize("hasAuthority('KPI_GOAL_FORMULA')")
    public Result getGoals(@AuthenticationPrincipal MyUserDetail user, GoalForm form) {
        return Result.success(kpiGoalService.getGoals(user, form));
    }

    /**
     * 目标下拉列表
     *
     * @param user
     * @param form
     * @return
     */
    @GetMapping("/goal-list")
    public Result getGoalList(@AuthenticationPrincipal MyUserDetail user, GoalForm form) {
        return Result.success(kpiGoalService.getGoalList(user, form));
    }

    /**
     * 编辑目标
     *
     * @param user
     * @param goalId
     * @param form
     * @return
     */
    @MyLog(value = "编辑目标")
    @PutMapping("/goal/edit/{goalId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_GOAL')")
    public Result editGoal(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer goalId, @RequestBody GoalForm form) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiGoalService.editGoal(goalId, form);
        return Result.success();
    }

    /**
     * 完成目标
     *
     * @param user
     * @param goalId
     * @return
     */
    @MyLog(value = "完成目标")
    @PutMapping("/goal/finish/{goalId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_GOAL')")
    public Result finishGoal(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer goalId) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiGoalService.finishGoal(goalId);
        return Result.success();
    }


    @MyLog(value = "删除目标")
    @DeleteMapping("/goal/delete/{goalId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_GOAL')")
    public Result deleteGoal(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer goalId) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiGoalService.deleteGoal(goalId);
        return Result.success();
    }


}
