package com.jty.performance.repository;

import com.jty.performance.domain.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: yeting
 * @Date: 2019/5/15 19:42
 */
public interface ParameterRepository extends JpaRepository<Parameter, Integer> {
}
