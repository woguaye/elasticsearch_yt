package com.jty.performance.domain.form;

import lombok.Data;

/**
 *
 */
@Data
public class KpiEmployeeImportDetail extends EmployeeForm {
    private boolean succeed;
    private String errorMessage;
}
