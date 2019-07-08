package com.jty.performance.domain;

import com.jty.performance.domain.dto.KpiIntegralDto;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 历史贡献变动日志
 *
 * @Author: yeting
 * @Date: 2019/4/25 16:55
 */
@Data
@Entity
@Table(name = "log_qualification")
public class LogQualification extends SuperEntity {

    /**
     * 历史贡献变动时间
     */
    @Column(name = "qualif_modify_time", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '积分变动时间'")
    @UpdateTimestamp
    private LocalDateTime qualifModifyTime;

    /**
     * 历史贡献变动量
     */
    private Integer qualifModifyNum;

    /**
     * 历史贡献变动原因
     */
    private String qualifModifyReason;


    /**
     * 员工对象
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Employee employee;


    /**
     * 积分变动类型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "integral_modify_type_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private IntegralModifyType integralModifyType;


    /**
     * 考核期ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "plan_cycle_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private PlanCycle planCycle;

    /**
     * 部门对象
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Department department;


    public KpiIntegralDto castToDto() {
        KpiIntegralDto kpiIntegralDto = new KpiIntegralDto();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        kpiIntegralDto.setChangeTime(df.format(this.qualifModifyTime));
        kpiIntegralDto.setChangeNum(this.qualifModifyNum);
        kpiIntegralDto.setChangeType(this.integralModifyType.getModifyType());
        kpiIntegralDto.setChangeReason(this.qualifModifyReason);
        return kpiIntegralDto;
    }
}
