package com.jty.performance.domain;

import com.jty.performance.domain.dto.KpiTaskTypeDto;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: yeting
 * @Date: 2019/5/22 17:27
 */
@Data
@Entity
@Table(name = "task_type")
public class TaskType extends SuperEntity {

    private String taskTypeName;

    public KpiTaskTypeDto castToDto() {
        KpiTaskTypeDto kpiTaskTypeDto = new KpiTaskTypeDto();
        kpiTaskTypeDto.setTaskTypeId(this.getId());
        kpiTaskTypeDto.setTaskTypeName(this.taskTypeName);
        return kpiTaskTypeDto;
    }
}
