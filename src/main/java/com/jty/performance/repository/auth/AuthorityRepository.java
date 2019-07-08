package com.jty.performance.repository.auth;

import com.jty.performance.domain.auth.AuthAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 权限Repository
 *
 * @author Jason
 * @since 2018/12/22 09:00
 */
public interface AuthorityRepository extends JpaRepository<AuthAuthority, Integer>, JpaSpecificationExecutor<AuthAuthority> {
}
