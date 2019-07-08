package com.jty.performance.service;

import com.jty.performance.domain.dto.KpiGoalDto;
import com.jty.performance.domain.form.GoalForm;
import com.jty.performance.domain.form.TaskForm;
import com.jty.performance.security.MyUserDetail;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * KpiGoalService
 *
 * @Author: yeting
 * @Date: 2019/6/27 9:25
 */
public interface KpiGoalService {
    /**
     * 创建目标
     *
     * @param user
     * @param form
     */
    void addGoal(MyUserDetail user, GoalForm form);

    /**
     * 根据目标ID查询目标详情
     *
     * @param goalId
     * @return
     */
    KpiGoalDto getGoalInfoById(Integer goalId);

    /**
     * 获取部门目标列表
     *
     * @param user
     * @param form
     * @return
     */
    Page<KpiGoalDto> getGoals(MyUserDetail user, GoalForm form);

    /**
     * 编辑目标
     *
     * @param goalId
     * @param form
     */
    void editGoal(Integer goalId, GoalForm form);

    /**
     * 完成目标
     *
     * @param goalId
     */
    void finishGoal(Integer goalId);

    /**
     * 删除目标
     *
     * @param goalId
     */
    void deleteGoal(Integer goalId);

    /**
     * 目标下拉选
     *
     * @param user
     * @param form
     * @return
     */
    List<KpiGoalDto> getGoalList(MyUserDetail user, GoalForm form);

}
