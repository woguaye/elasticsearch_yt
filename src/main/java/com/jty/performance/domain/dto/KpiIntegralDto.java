package com.jty.performance.domain.dto;

import com.jty.performance.domain.Integral;
import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/4/28 22:20
 */
@Data
public class KpiIntegralDto extends SuperDto {


    /**
     * 变动时间
     */
    private String changeTime;

    /**
     * 变动类型
     */
    private String changeType;

    /**
     * 变动量
     */
    private Integer changeNum;

    /**
     * 变动原因
     */
    private String changeReason;

}
