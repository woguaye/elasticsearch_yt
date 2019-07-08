package com.jty.performance.controller.kpi;

import com.jty.performance.annotation.MyLog;
import com.jty.performance.domain.form.TaskForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiTaskService;
import com.jty.performance.support.Result;
import com.jty.performance.support.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: yeting
 * @Date: 2019/5/9 21:13
 */
@RestController
@RequestMapping(value = "/kpi")
public class KpiTaskController {

    @Autowired
    private KpiTaskService kpiTaskService;

    /**
     * 创建任务
     *
     * @param form
     * @return
     */
    @MyLog(value = "任务创建")
    @PostMapping("/task/add/{userId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_TASK')")
    public Result createTask(@AuthenticationPrincipal MyUserDetail user, @RequestBody @Valid TaskForm form) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiTaskService.addTask(form);
        return Result.success();
    }


    /**
     * 任务审批列表
     *
     * @param form
     * @return
     */
    @GetMapping("/task-approval")
    @PreAuthorize("hasAuthority('KPI_TASK_APPROVAL')")
    public Result getTaskApprovalList(@AuthenticationPrincipal MyUserDetail user, TaskForm form) {
        return Result.success(kpiTaskService.getTaskApprovalList(user, form));
    }


    /**
     * 任务审批
     *
     * @param taskId
     * @param form
     * @return
     */
    @MyLog(value = "任务审批")
    @PutMapping("/task-approval/edit/{taskId}")
    @PreAuthorize("hasAuthority('KPI_TASK_APPROVAL')")
    public Result approvalTask(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer taskId, @RequestBody TaskForm form) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiTaskService.approvalTask(taskId, form);
        return Result.success();
    }


    /**
     * 任务管理列表
     *
     * @param form
     * @return
     */
    @GetMapping("/task-manage")
    @PreAuthorize("hasAuthority('KPI_MANAGE_TASK')")
    public Result getTaskManageList(@AuthenticationPrincipal MyUserDetail user, TaskForm form) {
        form.setDeptId(user.getDeptId());
        return Result.success(kpiTaskService.getTaskManageList(form));
    }


    /**
     * 任务申请验收
     *
     * @param taskId
     * @return
     */
    @PutMapping("/task-manage/applyaccept/{taskId}")
    @PreAuthorize("hasAuthority('KPI_MANAGE_TASK')")
    public Result applyAcceptTask(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer taskId) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            return Result.success();
        }
        kpiTaskService.applyAcceptTask(taskId);
        return Result.success();
    }


    /**
     * 任务验收列表
     *
     * @param form
     * @return
     */
    @GetMapping("/task-manage/accept")
    @PreAuthorize("hasAuthority('KPI_TASK_ACCEPT')")
    public Result getAcceptTaskList(@AuthenticationPrincipal MyUserDetail user, TaskForm form) {
        return Result.success(kpiTaskService.getTaskApprovalList(user, form));
    }

    /**
     * 任务验收
     *
     * @param taskId
     * @param form
     * @return
     */
    @MyLog(value = "任务验收")
    @PutMapping("/task-manage/accept/{taskId}")
    @PreAuthorize("hasAuthority('KPI_TASK_ACCEPT')")
    public Result acceptTask(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer taskId, @RequestBody TaskForm form) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiTaskService.acceptTask(taskId, form);
        return Result.success();
    }


    /**
     * 进行中任务列表
     *
     * @param form
     * @return
     */
    @GetMapping("/task/progress")
    @PreAuthorize("hasAuthority('KPI_TASK_FORMULA')")
    public Result getProgressTaskList(@AuthenticationPrincipal MyUserDetail user, TaskForm form) {
        return Result.success(kpiTaskService.getTaskApprovalList(user, form));
    }

    /**
     * 验收中任务列表
     *
     * @param form
     * @return
     */
    @GetMapping("/task/accept")
    @PreAuthorize("hasAuthority('KPI_TASK_FORMULA')")
    public Result getAcceptTaskOpenList(@AuthenticationPrincipal MyUserDetail user, TaskForm form) {
        return Result.success(kpiTaskService.getTaskApprovalList(user, form));
    }

    /**
     * 分配中任务列表
     *
     * @param form
     * @return
     */
    @GetMapping("/task/allot")
    @PreAuthorize("hasAuthority('KPI_TASK_FORMULA')")
    public Result getAllotTaskList(@AuthenticationPrincipal MyUserDetail user, TaskForm form) {
        return Result.success(kpiTaskService.getTaskApprovalList(user, form));
    }

    /**
     * 已完成任务列表
     *
     * @param form
     * @return
     */
    @GetMapping("/task/completed")
    @PreAuthorize("hasAuthority('KPI_TASK_FORMULA')")
    public Result getCompletedTaskList(@AuthenticationPrincipal MyUserDetail user, TaskForm form) {
        return Result.success(kpiTaskService.getTaskApprovalList(user, form));
    }

    /**
     * 悬赏中的任务
     *
     * @param user
     * @param form
     * @return
     */
    @GetMapping("/task/reward")
    @PreAuthorize("hasAuthority('KPI_TASK_FORMULA')")
    public Result getRewardTaskList(@AuthenticationPrincipal MyUserDetail user, TaskForm form) {
        return Result.success(kpiTaskService.getTaskApprovalList(user, form));
    }

    /**
     * 获取审批中和验收中任务数量
     *
     * @return
     */
    @GetMapping("/task/task-num")
    public Result getTaskNumByState(@AuthenticationPrincipal MyUserDetail user) {
        Integer deptId = user.getDeptId();
        return Result.success(kpiTaskService.getTaskNumByState(deptId));
    }

    /**
     * 任务删除
     *
     * @param taskId
     * @return
     */
    @MyLog(value = "删除任务")
    @PreAuthorize("hasAuthority('KPI_MANAGE_TASK')")
    @DeleteMapping("/task/{taskId}")
    public Result deleteTask(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer taskId) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiTaskService.deleteTask(taskId);
        return Result.success();
    }

    /**
     * 任务驳回
     *
     * @param taskId
     * @return
     */
    @PutMapping("/task-manage/reject/{taskId}")
    @PreAuthorize("hasAuthority('KPI_TASK_APPROVAL')")
    public Result rejectTask(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer taskId) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiTaskService.rejectTask(taskId);
        return Result.success();
    }

    /**
     * 获取任务类型列表
     *
     * @return
     */
    @GetMapping("/task/task-type")
    public Result getTaskTypes() {
        return Result.success(kpiTaskService.getTaskTypes());
    }

    /**
     * 获取审批中和验收中任务数量
     *
     * @return
     */
    @GetMapping("/task-allot/{userId}")
    public Result getAllotTaskNumByState(@PathVariable Integer userId) {
        return Result.success(kpiTaskService.getAllotTaskNumByState(userId));
    }


    /**
     * 获取任务详情
     *
     * @param taskId
     * @return
     */
    @GetMapping("/task/task-info/{taskId}")
    public Result getTaskInfoById(@PathVariable Integer taskId) {
        return Result.success(kpiTaskService.getTaskInfoById(taskId));

    }

    /**
     * 领取悬赏任务
     *
     * @param user
     * @param taskId
     * @param form
     * @return
     */
    @PutMapping("/task-approval/receive-reward/{taskId}")
    @PreAuthorize("hasAuthority('KPI_REWARD_RECEIVE')")
    public Result rewardTask(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer taskId, @RequestBody TaskForm form) {
        //设置浏览账号
        if ("41".equals(user.getUserId() + "")) {
            throw new BusinessException(ResultCode.USER_HAS_NOT_PERMISSION);
        }
        kpiTaskService.rewardTask(taskId, form);
        return Result.success();
    }


}
