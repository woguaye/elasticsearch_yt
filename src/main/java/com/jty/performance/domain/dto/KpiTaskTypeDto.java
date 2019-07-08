package com.jty.performance.domain.dto;

import lombok.Data;

/**
 * @Author: yeting
 * @Date: 2019/5/23 9:32
 */
@Data
public class KpiTaskTypeDto extends SuperDto {

    /**
     * 任务类型ID
     */
    private Integer taskTypeId;

    /**
     * 任务类型
     */
    private String taskTypeName;

}
