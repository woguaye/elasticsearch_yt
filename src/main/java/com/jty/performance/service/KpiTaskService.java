package com.jty.performance.service;

import com.jty.performance.domain.dto.KpiTaskDto;
import com.jty.performance.domain.dto.KpiTaskNumDto;
import com.jty.performance.domain.dto.KpiTaskTypeDto;
import com.jty.performance.domain.form.TaskForm;
import com.jty.performance.security.MyUserDetail;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/5/9 21:37
 */
public interface KpiTaskService {
    /**
     * 新建任务
     *
     * @param form
     */
    void addTask(TaskForm form);

    /**
     * 获取审核列表
     *
     * @param form
     * @return
     */
    Page<KpiTaskDto> getTaskApprovalList(MyUserDetail user, TaskForm form);

    /**
     * 任务审批
     *
     * @param taskId
     * @param form
     */
    void approvalTask(Integer taskId, TaskForm form);

    /**
     * 获取任务管理列表
     *
     * @param form
     * @return
     */
    Page<KpiTaskDto> getTaskManageList(TaskForm form);

    /**
     * 任务申请验收
     *
     * @param taskId
     */
    void applyAcceptTask(Integer taskId);

    /**
     * 任务验收
     *
     * @param taskId
     * @param form
     */
    void acceptTask(Integer taskId, TaskForm form);


    KpiTaskNumDto getTaskNumByState(Integer deptId);

    /**
     * 删除任务
     *
     * @param taskId
     */
    void deleteTask(Integer taskId);

    /**
     * 任务驳回
     *
     * @param taskId
     */
    void rejectTask(Integer taskId);

    /**
     * 任务类型列表
     *
     * @return
     */
    List<KpiTaskTypeDto> getTaskTypes();

    /**
     * 获取用户的待分配任务数量
     *
     * @param userId
     * @return
     */
    KpiTaskNumDto getAllotTaskNumByState(Integer userId);


    /**
     * 获取任务详情
     *
     * @param taskId
     * @return
     */
    KpiTaskDto getTaskInfoById(Integer taskId);

    /**
     * 领取悬赏任务
     *
     * @param taskId
     * @param form
     */
    void rewardTask(Integer taskId, TaskForm form);
}
