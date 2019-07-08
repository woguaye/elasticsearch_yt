package com.jty.performance.domain.dto.auth;

import com.jty.performance.domain.dto.SuperDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * AuthAuthorityDto
 *
 * @author Manjiajie
 * @since 2019-2-13 19:19:46
 */
@Data
public class AuthAuthorityDto extends SuperDto {

    private String code;

    private String name;

    private String url;

    private Integer depth;

    private String ordinal;

    private String  pathIds;



}
