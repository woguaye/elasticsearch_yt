package com.jty.performance.domain.auth;

import com.jty.performance.domain.SuperEntity;
import com.jty.performance.domain.dto.auth.AuthAuthorityDto;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 权限
 *
 * @author Jason
 * @since 2018/12/19 14:44
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "platform", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("authority")
public class AuthAuthority extends SuperEntity {

    private String code;

    private String name;

    private String url;

    private Integer depth;

    private String ordinal;

    private String pathIds;

    @ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY)
    private List<AuthRole> roles;

    public AuthAuthorityDto castToDto() {
        AuthAuthorityDto authAuthorityDto = new AuthAuthorityDto();
        authAuthorityDto.setId(this.getId());
        authAuthorityDto.setName(name);
        return authAuthorityDto;
    }
}
