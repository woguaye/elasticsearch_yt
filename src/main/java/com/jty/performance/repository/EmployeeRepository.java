package com.jty.performance.repository;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.Employee;
import com.jty.performance.domain.Integral;
import com.jty.performance.domain.auth.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/4/27 17:53
 */
public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    @Query(value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\temployee a\n" +
            "LEFT JOIN auth_user b ON a.user_id = b.id\n" +
            "LEFT JOIN auth_relation_user_role c ON b.id = c.user_id\n" +
            "LEFT JOIN department d ON a.department_id = d.id\n" +
            "WHERE\n" +
            "\tb.enabled = TRUE\n" +
            "AND c.role_id = 2\n" +
            "AND d.parent_id = ?1\n" +
            "ORDER BY\n" +
            "\ta.current_score DESC,\n" +
            "\ta.record_score DESC,\n" +
            "\ta.staff_code ASC", nativeQuery = true)
    public List<Employee> findOrderByCurrentScore(Integer parentId);

    List<Employee> findByDepartment(Department department);

    @Query(value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\temployee a\n" +
            "LEFT JOIN department d ON a.department_id = d.id\n" +
            "WHERE\n" +
            "d.parent_id = ?1", nativeQuery = true)
    List<Employee> findByParentDepartment(Integer parentId);

    Optional<Employee> findByAuthUser(AuthUser authUser);

}
