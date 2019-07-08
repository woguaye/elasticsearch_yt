package com.jty.performance.domain.auth;

import com.jty.performance.domain.SuperEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限用户
 *
 * @author Jason
 * @since 2018/12/19 14:43
 */
@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class AuthUser extends SuperEntity {

    private String account;

    private String mobile;

    private String password;

    private Boolean enabled = true;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "auth_relation_user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<AuthRole> roles = new ArrayList<>();

    public void addRole(AuthRole authRole) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(authRole);
    }

}
