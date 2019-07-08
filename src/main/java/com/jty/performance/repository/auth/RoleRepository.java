package com.jty.performance.repository.auth;

import com.jty.performance.domain.auth.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/6/13 9:01
 */
public interface RoleRepository extends JpaRepository<AuthRole, Integer> {
    
    Optional<AuthRole> findByName(String name);
}
