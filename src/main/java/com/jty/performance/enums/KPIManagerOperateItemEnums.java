package com.jty.performance.enums;

import lombok.Getter;

/**
 * KPIManagerOperateItemEnums
 *
 * @author Manjiajie
 * @since 2019-2-13 19:19:46
 */
@Getter
public enum KPIManagerOperateItemEnums {
    ENABLE(1, "启用"),
    DISABLE(2, "禁用"),
    ;

    KPIManagerOperateItemEnums(int num, String docs) {
        this.num = num;
        this.docs = docs;
    }

    private int num;
    private String docs;

}
