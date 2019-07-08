package com.jty.performance.repository;

import com.jty.performance.domain.Employee;
import com.jty.performance.domain.LogIntegral;
import com.jty.performance.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/4/27 17:58
 */
public interface LogIntegralRepository extends JpaRepository<LogIntegral, Integer>, JpaSpecificationExecutor<LogIntegral> {

    Optional<LogIntegral> findByEmployeeAndTask(Employee employee, Task task);

}
