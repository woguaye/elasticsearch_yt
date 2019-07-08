package com.jty.performance.repository;

import com.jty.performance.domain.IntegralHistoryList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/5/10 16:49
 */
public interface IntegralHistoryListRepository extends JpaRepository<IntegralHistoryList, Integer>, JpaSpecificationExecutor<IntegralHistoryList> {

    @Query(value = "select MAX(rank) from integral_history_list where plan_cycle_id = ?1", nativeQuery = true)
    Integer findByPlanCycleIdAndRankMax(Integer planCycleId);
}
