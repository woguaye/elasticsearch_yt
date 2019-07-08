package com.jty.performance.domain.auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jty.performance.domain.SuperEntity;
import com.jty.performance.domain.dto.auth.AuthRoleDto;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户角色
 *
 * @author Jason
 * @since 2018/12/19 14:44
 */
@Entity
@Data
public class AuthRole extends SuperEntity {

    private String name;

    private Integer parentId = 0;

    private Integer depth = 1;

    private Integer ordinal = 1;

    private String pathIds = "";

    private String platform = "";

    private String dtype = "";

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    @JsonBackReference("users")
    private List<AuthUser> users;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "auth_relation_role_authority",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id")})
    private Set<AuthAuthority> authorities;

    public void addAuthority(AuthAuthority authority) {
        if (authorities == null) {
            authorities = new HashSet<>();
        }
        authorities.add(authority);
    }

    public AuthRoleDto cashToDto() {
        AuthRoleDto roleDto = new AuthRoleDto();
        List<Integer> authAuthorityIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(this.authorities)) {
            this.authorities.forEach(authority -> authAuthorityIds.add(authority.getId()));
        }
        roleDto.setAuthorityIds(authAuthorityIds);
        roleDto.setName(name);
        roleDto.setId(getId());
        return roleDto;
    }
}
