package com.jty.performance.domain;

import com.jty.performance.domain.dto.KpiPlanCycleDto;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author: yeting
 * @Date: 2019/5/7 13:59
 */
@Data
@Entity
@Table(name = "plan_cycle")
public class PlanCycle extends SuperEntity {

    /**
     * 考核期名称
     */
    private String planCycleName;

    /**
     * 考核期状态 0：已结束
     * 1:进行中
     */
    private Integer planCycleState;


    /**
     * 考核期内已使用积分
     */
    private Integer planCycleUseScore;

    /**
     * 考核期开始时间
     */
    @Column(name = "start_time", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间'")
    private LocalDate startTime;

    /**
     * 考核期结束时间
     */
    private LocalDate endTime;

    /**
     * 部门信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Department department;

    /**
     * 考核期数
     */
    private Integer count;

    public KpiPlanCycleDto castToDto() {
        KpiPlanCycleDto kpiPlanCycleDto = new KpiPlanCycleDto();

        kpiPlanCycleDto.setPlanCycleId(this.getId());
        kpiPlanCycleDto.setPlanCycleName(this.planCycleName);
        kpiPlanCycleDto.setPlanCycleStart(this.startTime);
        kpiPlanCycleDto.setPlanCycleEnd(this.endTime);
        kpiPlanCycleDto.setPlanCycleScore(this.planCycleUseScore);
        kpiPlanCycleDto.setCount(this.count);
        kpiPlanCycleDto.setDeptId(this.department.getId());
        return kpiPlanCycleDto;
    }
}
