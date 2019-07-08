package com.jty.performance.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 自定义UserDetail
 *
 * @author Jason
 * @since 2018/12/22 09:10
 */
@Data
public class MyUserDetail implements UserDetails {


    private Integer userId;

    private Collection<? extends GrantedAuthority> authorities;

    private String password;

    private String username;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled = true;

    private List<String> modules;

    /**
     * 本期排名
     */
    private Integer rank;

    /**
     * 本期积分
     */
    private Integer quarterIntegra;

    /**
     * 历史贡献值
     */
    private Integer qualification;

    /**
     * 是否签到
     */
    private Boolean isSigned;

    /**
     * 部门ID
     */
    private Integer deptId;


}
