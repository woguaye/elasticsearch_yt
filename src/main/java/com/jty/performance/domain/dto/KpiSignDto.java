package com.jty.performance.domain.dto;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/5/27 19:24
 */
@Data
public class KpiSignDto extends SuperDto {

    /**
     * 签到获得积分
     */
    private Integer signScore;
}
