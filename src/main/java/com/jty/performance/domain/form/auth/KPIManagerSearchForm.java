package com.jty.performance.domain.form.auth;

import com.jty.performance.domain.form.PageableForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * KPIManagerSearchForm
 *
 * @author Jason
 * @since 2019/1/17 13:38
 */
@Data
public class KPIManagerSearchForm extends PageableForm {

    private Integer roleId;

    private Boolean enabled;


    private String account;

    /**
     * 部门ID
     */
    private String deptId;
}
