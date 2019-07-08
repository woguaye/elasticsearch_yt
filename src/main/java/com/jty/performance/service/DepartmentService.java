package com.jty.performance.service;

import com.jty.performance.domain.dto.DepartmentTreeDto;
import com.jty.performance.domain.dto.KpiDepartmentDto;
import com.jty.performance.domain.form.DepartmentForm;
import com.jty.performance.security.MyUserDetail;

import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/6/17 9:24
 */
public interface DepartmentService {

    /**
     * 新建中心部门
     *
     * @param form
     * @return
     */
    KpiDepartmentDto addDepartmentCore(DepartmentForm form);


    /**
     * 创建中心子部门
     *
     * @param deptIdCore
     * @param form
     * @return
     */
    KpiDepartmentDto addDepartmentChild(Integer deptIdCore, DepartmentForm form);

    /**
     * 获取部门树
     *
     * @param user
     * @return
     */
    List<DepartmentTreeDto> getDepartmentsByAuth(MyUserDetail user);


    /**
     * 编辑部门信息
     *
     * @param deptId
     * @param form
     * @return
     */
    KpiDepartmentDto alterDepartment(Integer deptId, DepartmentForm form);

    /**
     * 获取部门树
     *
     * @return
     */
    List<DepartmentTreeDto> getDepartmentCores();
}
