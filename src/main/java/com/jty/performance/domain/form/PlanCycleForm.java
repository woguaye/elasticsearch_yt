package com.jty.performance.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * @Author: yeting
 * @Date: 2019/5/7 16:10
 */
@Data
public class PlanCycleForm {

    /**
     * 考核期ID
     */
    private Integer planCycleId;


    /**
     * 考核期结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate planCycleEnd;

}
