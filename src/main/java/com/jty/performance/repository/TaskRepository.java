package com.jty.performance.repository;

import com.jty.performance.domain.Employee;
import com.jty.performance.domain.Goal;
import com.jty.performance.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/5/9 21:41
 */
public interface TaskRepository extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task> {

    @Query(value = "SELECT count(*) FROM task where task_state = ?1 and task_flag = true and department_id = ?2", nativeQuery = true)
    Integer findCountByTaskState(Integer taskState, Integer deptId);

    @Query(value = "SELECT count(*) FROM task where task_state = ?1 and employee_id = ?2 and task_flag = true", nativeQuery = true)
    Integer findCountByUserTaskState(Integer taskState, Integer userId);

    @Query(value = "select * from task where estimated_score is NOT NULL AND  task_start_time BETWEEN ?1 AND ?2 ", nativeQuery = true)
    List<Task> findTaskByTime(LocalDate startTime, LocalDate endTime);

    List<Task> findByEmployee(Employee employee);

    List<Task> findByGoalAndTaskFlag(Goal goal, Boolean taskFlag);
}
