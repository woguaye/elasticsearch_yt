package com.jty.performance.repository;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.PlanCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/5/7 15:26
 */
public interface PlanCycleRepository extends JpaRepository<PlanCycle, Integer> {

    List<PlanCycle> findByPlanCycleState(Integer planCycleState);

    Optional<PlanCycle> findByPlanCycleStateAndDepartment(Integer planCycleState, Department department);

    Optional<PlanCycle> findByCountAndDepartment(Integer count, Department department);

    List<PlanCycle> findByDepartmentOrderByCountAsc(Department department);
}
