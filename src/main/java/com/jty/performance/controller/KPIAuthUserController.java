package com.jty.performance.controller;

import com.jty.performance.support.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * KPIAuthUserController
 *
 * @Author: yeting
 * @Date: 2019/4/23 21:53
 */
@RestController
@RequestMapping(value = "/kpi/users")
public class KPIAuthUserController {

    @GetMapping("/{userId}")
    public Result findSchoolTeacher(@PathVariable Integer userId) {
        return Result.success(userId);
    }
}
