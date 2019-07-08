package com.jty.performance.repository;

import com.jty.performance.domain.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: yeting
 * @Date: 2019/5/24 20:39
 */
public interface SysLogRepository extends JpaRepository<SysLog, Integer> {
}
