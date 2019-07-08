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
 * 积分变动日志表
 *
 * @Author: yeting
 * @Date: 2019/4/25 21:39
 */
@Data
@Entity
@Table(name = "log_integral")
public class LogIntegral extends SuperEntity {

    /**
     * 变动时间
     */
    @Column(name = "integ_modify_time", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '积分变动时间'")
    @UpdateTimestamp
    private LocalDateTime integModifyTime;

    /**
     * 积分变动量
     */
    private Integer integModifyNum;

    /**
     * 积分变动原因
     */
    private String integModifyReason;


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
     * 任务ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Task task;


    public KpiIntegralDto castToDto() {
        KpiIntegralDto kpiIntegralDto = new KpiIntegralDto();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        kpiIntegralDto.setChangeTime(df.format(this.integModifyTime));
        kpiIntegralDto.setChangeNum(this.integModifyNum);
        kpiIntegralDto.setChangeType(this.integralModifyType.getModifyType());
        kpiIntegralDto.setChangeReason(this.integModifyReason);
        return kpiIntegralDto;
    }
}
