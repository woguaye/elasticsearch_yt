package com.jty.performance.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/5/10 19:14
 */
@Data
public class StaffNodeDto extends SuperDto {

    /**
     * 部门名称
     */
    private String name;


    /**
     * 所有的子部门
     */
    private List<StaffNodeDto> children;


    /**
     * 部门下的所有成员
     */
    private List<KpiEmployeeDto> employeeList;

    /**
     * 父节点ID
     */
    private Integer parentId;

}
