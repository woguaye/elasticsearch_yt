package com.jty.performance.repository;

import com.jty.performance.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/4/27 17:56
 */
public interface DepartmentRepository extends JpaRepository<Department, Integer>, JpaSpecificationExecutor<Department> {

    List<Department> findAllByParentId(Integer parentId);

    Optional<Department> findByParentIdAndDeptName(Integer parentId, String deptName);
}
