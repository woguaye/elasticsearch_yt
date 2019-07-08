package com.jty.performance.domain.dto.auth;

import com.jty.performance.domain.dto.SuperDto;
import lombok.Data;

import java.util.List;

/**
 * AuthRoleDto
 *
 * @author Manjiajie
 * @since 2019-2-13 19:19:46
 */
@Data
public class AuthRoleDto extends SuperDto {
    private List<Integer> authorityIds;
    private String name;
}
