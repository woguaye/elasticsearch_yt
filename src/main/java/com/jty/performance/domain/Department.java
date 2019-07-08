package com.jty.performance.domain;

import com.jty.performance.domain.dto.KpiDepartmentDto;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门表
 *
 * @Author: yeting
 * @Date: 2019/4/25 16:55
 */
@Data
@Entity
@Table(name = "department")
public class Department extends SuperEntity {

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码
     */
    private String deprCode;

    /**
     * 父ID
     */
    private Integer parentId;


    /**
     * 员工信息
     */
    @OneToMany(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private List<Employee> employees;

    /**
     * 部门所在的所有子部门集合
     */
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Department> children = new ArrayList<Department>();

    public KpiDepartmentDto castToDto() {
        KpiDepartmentDto kpiDepartmentDto = new KpiDepartmentDto();
        kpiDepartmentDto.setId(this.getId());
        kpiDepartmentDto.setName(deptName);
        return kpiDepartmentDto;
    }
}
