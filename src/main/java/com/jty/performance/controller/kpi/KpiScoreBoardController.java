package com.jty.performance.controller.kpi;

import com.jty.performance.domain.form.KpiScoreBoardForm;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiScoreBoardService;
import com.jty.performance.support.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分排行榜控制层
 *
 * @Author: yeting
 * @Date: 2019/4/26 21:53
 */
@RestController
@RequestMapping(value = "/kpi")
public class KpiScoreBoardController {

    @Autowired
    private KpiScoreBoardService kpiScoreBoardService;

    /**
     * 查看积分排行榜
     *
     * @param form
     * @return
     */
    @GetMapping("/scoreboard")
    @PreAuthorize("hasAuthority('KPI_SCORE_RANK')")
    public Result getScoreBoardPage(@AuthenticationPrincipal MyUserDetail user, KpiScoreBoardForm form) {
        return Result.success(kpiScoreBoardService.getScoreBoard(user, form));
    }


    /**
     * 获取当前用户排行信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/userinfo/{userId}")
    public Result getUserInfo(@PathVariable Integer userId) {
        return Result.success(kpiScoreBoardService.getUserInfo(userId));
    }


}
