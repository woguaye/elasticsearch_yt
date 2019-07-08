package com.jty.performance.service;

import com.jty.performance.domain.dto.KpiPlanCycleDto;
import com.jty.performance.domain.form.PlanCycleForm;
import com.jty.performance.security.MyUserDetail;

import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/5/7 15:19
 */
public interface KpiPlanCycleService {

    /**
     * 查看考核期详情
     *
     * @return
     */
    KpiPlanCycleDto getPlanCycleInfo(MyUserDetail user);

    /**
     * 修改考核期结束时间
     *
     * @param planCycleId
     * @param form
     */
    void updatePlanCycleTime(Integer planCycleId, PlanCycleForm form);


    /**
     * 当前考核期积分预算
     *
     * @param planCycleId
     * @return
     */
    KpiPlanCycleDto getBudgetScore(MyUserDetail user, Integer planCycleId);

    /**
     * 获取历史考核期
     *
     * @return
     */
    List<KpiPlanCycleDto> getPlanCyclePast(MyUserDetail user);

    /**
     * 根据考核期ID获取考核期详情
     *
     * @param user
     * @param planCycleId
     * @return
     */
    KpiPlanCycleDto getPlanCycleInfoById(MyUserDetail user, Integer planCycleId);

    /**
     * 获取历史考核期
     *
     * @param deptId
     * @return
     */
    List<KpiPlanCycleDto> getPlanCycleByDeptId(Integer deptId);

}
