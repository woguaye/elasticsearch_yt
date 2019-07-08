package com.jty.performance.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * 员工签到表
 *
 * @Author: yeting
 * @Date: 2019/5/27 9:14
 */
@Data
@Entity
@Table(name = "log_staff_sign")
public class LogStaffSign extends SuperEntity {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 签到日期
     */
    private LocalDate signTime;

    /**
     * 考核期ID
     */
    private Integer planCycleId;

}
