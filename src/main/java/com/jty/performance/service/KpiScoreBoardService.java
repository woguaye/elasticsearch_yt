package com.jty.performance.service;

import com.jty.performance.domain.dto.KpiScoreBoardDto;
import com.jty.performance.domain.form.KpiScoreBoardForm;
import com.jty.performance.security.MyUserDetail;
import org.springframework.data.domain.Page;

/**
 * @Author: yeting
 * @Date: 2019/4/26 21:53
 */
public interface KpiScoreBoardService {

    /**
     * 查看排行榜信息
     *
     * @param form
     * @return
     */
    Page<KpiScoreBoardDto> getScoreBoard(MyUserDetail user, KpiScoreBoardForm form);

    /**
     * 查看用户排行信息
     *
     * @param userId
     * @return
     */
    KpiScoreBoardDto getUserInfo(Integer userId);


}
