package com.jty.performance.repository;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/6/27 10:12
 */
public interface GoalRepository extends JpaRepository<Goal, Integer>, JpaSpecificationExecutor<Goal> {

    List<Goal> findByDepartmentAndGoalFlagAndGoalState(Department department, Boolean goalFlag, Integer goalState);
}
