package com.jty.performance.domain;

import com.jty.performance.domain.auth.AuthRole;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.dto.KpiScoreBoardDto;
import com.jty.performance.domain.dto.auth.KPIManagerDTO;
import com.jty.performance.domain.dto.auth.KPIRoleDto;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.support.DomainRegistry;
import com.jty.performance.support.ResultCode;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户表
 *
 * @Author: yeting
 * @Date: 2019/4/23 22:52
 */
@Data
@Entity
@Table(name = "employee")
public class Employee extends SuperEntity {

    /**
     * 员工名称
     */
    private String eName;

    /**
     * 职位名称
     */
    private String ePosition;


    /**
     * 本考核期积分
     */
    private Integer currentScore;


    /**
     * 历史贡献积分
     */
    private Integer recordScore;


    /**
     * 员工编码
     */
    private String staffCode;

    /**
     * 和积分历史排行表关联
     */
    @OneToMany(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private List<IntegralHistoryList> integralHistoryList;


    @OneToMany(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private List<LogQualification> logQualification;


    /**
     * 部门信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Department department;

    /**
     * 用户信息
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private AuthUser authUser;

    /**
     * 用户对应的任务
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "employees")
    private List<Task> tasks = new ArrayList<>();

    public KpiScoreBoardDto castToDto(Integer count) {
        KpiScoreBoardDto kpiScoreBoardDto = new KpiScoreBoardDto();
        kpiScoreBoardDto.setUserId(this.getId());
        kpiScoreBoardDto.setUserName(this.eName);
        kpiScoreBoardDto.setPosition(this.ePosition);
        kpiScoreBoardDto.setQuarterIntegra(this.currentScore);
        kpiScoreBoardDto.setQualification(this.recordScore);
        List<IntegralHistoryList> integralHistoryLists = this.integralHistoryList;
        if (CollectionUtils.isNotEmpty(integralHistoryLists)) {
            if (count > 1) {
                Department parentDept = new Department();
                parentDept.setId(department.getParentId());
                PlanCycle planCycle = DomainRegistry.planCycleRepository().findByCountAndDepartment(count - 1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
                for (IntegralHistoryList integralHistoryList : integralHistoryLists) {
                    if (integralHistoryList.getEmployee().getId().equals(this.getId()) && integralHistoryList.getPlanCycleId().equals(planCycle.getId())) {
                        kpiScoreBoardDto.setPreRank(integralHistoryList.getRank());
                        kpiScoreBoardDto.setPreQuarterIntegra(integralHistoryList.getScore());
                    }
                }
            }
        } else {
            kpiScoreBoardDto.setPreRank(0);
            kpiScoreBoardDto.setPreQuarterIntegra(0);
        }
        return kpiScoreBoardDto;
    }

    public KpiScoreBoardDto castToDtoInfo() {
        KpiScoreBoardDto kpiScoreBoardDto = new KpiScoreBoardDto();
        kpiScoreBoardDto.setUserId(this.getId());
        kpiScoreBoardDto.setUserName(this.eName);
        kpiScoreBoardDto.setQuarterIntegra(this.currentScore);
        kpiScoreBoardDto.setQualification(this.recordScore);
        return kpiScoreBoardDto;
    }

    /**
     * 系统管理
     *
     * @return
     */
    public KPIManagerDTO castToManagerDto() {
        KPIManagerDTO kpiManagerDTO = new KPIManagerDTO();
        kpiManagerDTO.setId(authUser.getId());
        kpiManagerDTO.setAccount(authUser.getAccount());
        kpiManagerDTO.setRealName(eName);
        kpiManagerDTO.setPosition(ePosition);
        kpiManagerDTO.setEnabled(authUser.getEnabled());
        List<AuthRole> roles = authUser.getRoles();
        ArrayList<KPIRoleDto> kpiRoleDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            for (AuthRole authRole : roles) {
                KPIRoleDto kpiRoleDto = new KPIRoleDto();
                kpiRoleDto.setId(authRole.getId());
                kpiRoleDto.setName(authRole.getName());
                kpiRoleDtos.add(kpiRoleDto);
            }
        }
        ArrayList<Integer> deptIds = new ArrayList<>();
        deptIds.add(department.getParentId());
        deptIds.add(department.getId());
        kpiManagerDTO.setDeptIds(deptIds);
        kpiManagerDTO.setRoles(kpiRoleDtos);
        return kpiManagerDTO;
    }
}
