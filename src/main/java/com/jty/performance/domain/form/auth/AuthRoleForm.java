package com.jty.performance.domain.form.auth;


import com.jty.performance.domain.form.PageableForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

/**
 * Role添加Form
 *
 * @author Jason
 * @since 2018/12/27 19:50
 */
@Data
public class AuthRoleForm extends PageableForm {

    @Length(max = 50, message = "名字不能超过50个字符")
    private String name;
    private Set<Integer> authAuthoritiesIds;

    private Integer userId;
}
