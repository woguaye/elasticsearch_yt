package com.jty.performance.service.auth;

import com.jty.performance.domain.dto.auth.KPIManagerDTO;
import com.jty.performance.domain.dto.auth.KPIManagerHistoryDto;
import com.jty.performance.domain.form.auth.KPIManagerHistoryForm;
import com.jty.performance.domain.form.auth.KPIManagerSearchForm;
import com.jty.performance.domain.form.auth.KPIStaffForm;
import com.jty.performance.security.MyUserDetail;
import org.springframework.data.domain.Page;

/**
 * @Author: yeting
 * @Date: 2019/5/22 14:18
 */
public interface KPIManagerService {

    /**
     * 获取用户列表
     *
     * @param user
     * @param form
     * @return
     */
    Page<KPIManagerDTO> getManagers(MyUserDetail user, KPIManagerSearchForm form);

    /**
     * 添加用户
     *
     * @param form
     * @return
     */
    void addStaff(KPIStaffForm form);

    /**
     * 更新用户信息
     *
     * @param form
     */
    void updateStaff(KPIStaffForm form);

    /**
     * 重置密码
     *
     * @param staffId
     */
    void resetPassword(Integer staffId);

    /**
     * 启用用户
     *
     * @param staffId
     * @param userId
     * @param operateReason
     */
    void enableManager(Integer staffId, Integer userId, String operateReason);

    /**
     * 禁用用户
     *
     * @param staffId
     * @param userId
     * @param operateReason
     */
    void disableManager(Integer staffId, Integer userId, String operateReason);

    /**
     * 获取用户被操作记录
     *
     * @param staffId
     * @param form
     * @return
     */
    Page<KPIManagerHistoryDto> findHistoryByManagerId(Integer staffId, KPIManagerHistoryForm form);

    /**
     * 删除用户
     *
     * @param staffId
     */
    void deleteStaff(Integer staffId);
}
