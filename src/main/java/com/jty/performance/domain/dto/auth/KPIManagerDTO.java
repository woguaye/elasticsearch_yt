package com.jty.performance.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jty.performance.domain.dto.SuperDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jason
 * @since 2019/1/17 10:47
 */
@Data
public class KPIManagerDTO extends SuperDto {

    private String account;

    private String realName;

    private Boolean enabled;

    private String position;

    private List<KPIRoleDto> roles;

    private List<Integer> deptIds;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
}
