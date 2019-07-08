package com.jty.performance.domain;

import com.jty.performance.domain.dto.KpiScoreBoardDto;
import lombok.Data;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * 积分历史排名
 *
 * @Author: yeting
 * @Date: 2019/5/7 14:34
 */
@Data
@Entity
public class IntegralHistoryList extends SuperEntity {

    /**
     * 考核期ID
     */
    private Integer planCycleId;

    /**
     * 员工ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Employee employee;

    /**
     * 部门ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Department department;

    /**
     * 积分
     */
    private Integer score;

    /**
     * 排名
     */
    private Integer rank;

    public KpiScoreBoardDto castToDto(Integer planCycleId) {
        KpiScoreBoardDto kpiScoreBoardDto = new KpiScoreBoardDto();
        kpiScoreBoardDto.setRank(this.getRank());
        kpiScoreBoardDto.setUserId(this.getEmployee().getId());
        kpiScoreBoardDto.setUserName(this.getEmployee().getEName());
        kpiScoreBoardDto.setPosition(this.getEmployee().getEPosition());
        kpiScoreBoardDto.setQuarterIntegra(this.score);
        List<IntegralHistoryList> integralHistoryLists = this.getEmployee().getIntegralHistoryList();
        if (CollectionUtils.isNotEmpty(integralHistoryLists)) {
            for (IntegralHistoryList integralHistoryList : integralHistoryLists) {
                if (integralHistoryList.getPlanCycleId().equals(planCycleId - 1)) {
                    kpiScoreBoardDto.setPreRank(integralHistoryList.getRank());
                    kpiScoreBoardDto.setPreQuarterIntegra(integralHistoryList.getScore());
                }
            }
        }
        List<LogQualification> logQualifications = this.getEmployee().getLogQualification();
        if (CollectionUtils.isNotEmpty(logQualifications)) {
            for (LogQualification logQualification : logQualifications) {
                if (planCycleId.equals(logQualification.getPlanCycle().getId())) {
                    kpiScoreBoardDto.setQualification(logQualification.getQualifModifyNum());
                }
            }
        }
        return kpiScoreBoardDto;
    }
}
