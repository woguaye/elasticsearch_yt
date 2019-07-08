package com.jty.performance.repository;

import com.jty.performance.domain.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: yeting
 * @Date: 2019/5/23 9:57
 */
public interface TaskTypeRepository extends JpaRepository<TaskType, Integer> {
}
