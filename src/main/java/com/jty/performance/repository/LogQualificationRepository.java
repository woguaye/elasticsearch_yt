package com.jty.performance.repository;

import com.jty.performance.domain.LogQualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: yeting
 * @Date: 2019/4/27 18:04
 */
public interface LogQualificationRepository extends JpaRepository<LogQualification, Integer>, JpaSpecificationExecutor<LogQualification> {
}
