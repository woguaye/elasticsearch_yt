package com.jty.performance.repository.auth;

import com.jty.performance.domain.auth.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 用户Repository
 *
 * @author Jason
 * @since 2018/12/22 08:56
 */
public interface UserRepository extends JpaRepository<AuthUser,Integer> {

    Optional<AuthUser> findByAccount(String account);

}
