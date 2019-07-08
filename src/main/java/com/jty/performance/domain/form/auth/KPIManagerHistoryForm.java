package com.jty.performance.domain.form.auth;

import com.jty.performance.domain.form.PageableForm;
import lombok.Data;

/**
 *
 *
 * @author Manjiajie
 * @since 2019-4-29 10:04:30
 */
@Data
public class KPIManagerHistoryForm extends PageableForm {

    private String operateReason;
}
