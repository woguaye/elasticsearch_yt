package com.jty.performance.repository;

import com.jty.performance.domain.LogStaffSign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/5/27 10:39
 */
public interface LogStaffSignRepository extends JpaRepository<LogStaffSign, Integer> {

    Optional<LogStaffSign> findByUserIdAndSignTime(Integer userId, LocalDate signTime);
}
