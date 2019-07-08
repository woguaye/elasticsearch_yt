package com.jty.performance.service;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.dto.KpiDeptIntegralDto;
import com.jty.performance.domain.dto.KpiIntegralDto;
import com.jty.performance.domain.dto.KpiSignDto;
import com.jty.performance.domain.dto.StaffNodeDto;
import com.jty.performance.domain.form.AcceptEmployeeForm;
import com.jty.performance.domain.form.KpiScoreBoardForm;
import com.jty.performance.security.MyUserDetail;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/4/28 22:32
 */
public interface KpiIntegralService {

    /**
     * 查看用户积分变动详情
     *
     * @param form
     * @return
     */
    Page<KpiIntegralDto> findIntegralInfo(KpiScoreBoardForm form);


    /**
     * 查看用户历史贡献值变动情况
     *
     * @param form
     * @return
     */
    Page<KpiIntegralDto> findRecordInfo(KpiScoreBoardForm form);


    /**
     * 部门员工树形结构
     *
     * @param
     * @return
     */
    List<StaffNodeDto> findEmployeeNodes(MyUserDetail user);

    /**
     * 个人积分划分
     *
     * @param eId
     * @param acceptEmployee
     */
    void divideScoreToEmployee(MyUserDetail user, Integer eId, AcceptEmployeeForm acceptEmployee);

    /**
     * 任务积分划分
     *
     * @param taskId
     * @param acceptEmployee
     */
    void divideTaskScoreToEmployee(MyUserDetail user, Integer taskId, AcceptEmployeeForm acceptEmployee);

    /**
     * 用户签到
     *
     * @param userId
     */
    KpiSignDto staffSign(Integer userId);

    /**
     * 用户签到积分列表
     *
     * @param form
     * @return
     */
    Page<KpiIntegralDto> findEmployeeSign(KpiScoreBoardForm form);

}
