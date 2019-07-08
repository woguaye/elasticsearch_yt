package com.jty.performance.domain.dto;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/5/13 16:50
 */
@Data
public class KpiTaskNumDto extends SuperDto {

    /**
     * 审批中任务数
     */
    private Integer taskApprovalNum;

    /**
     * 验收中任务数
     */
    private Integer taskAcceptNum;

    /**
     * 分配中任务数
     */
    private Integer taskAllotNum;
}
