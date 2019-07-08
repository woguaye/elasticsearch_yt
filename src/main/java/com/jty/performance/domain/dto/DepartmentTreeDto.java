package com.jty.performance.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Zhongwentao
 * @since 2019/1/4 9:49
 */
@Data
public class DepartmentTreeDto extends SuperDto {

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父节点id
     */
    private Integer parentId;


    private List<DepartmentTreeDto> children;
}
