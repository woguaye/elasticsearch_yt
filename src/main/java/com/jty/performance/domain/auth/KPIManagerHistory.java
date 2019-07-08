package com.jty.performance.domain.auth;


import com.jty.performance.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yeting
 * @since 2019-4-28 14:31:25
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "kpi_manager_history")
public class KPIManagerHistory extends SuperEntity {
    /**
     * 操作人id
     */
    private Integer operatorId;
    /**
     * 操作人的名字
     */
    private String operatorName;
    /**
     * 被操作对象id
     */
    private Integer managerId;
    /**
     * 操作项
     */
    private String operation;
    /**
     * 操作原因
     */
    private String operateReason;
}
