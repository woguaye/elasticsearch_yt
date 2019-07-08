package com.jty.performance.repository;

import com.jty.performance.domain.auth.KPIManagerHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: yeting
 * @Date: 2019/6/14 14:21
 */
public interface KPIManagerHistoryRepository extends JpaRepository<KPIManagerHistory, Integer> {
}
