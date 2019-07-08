package com.jty.performance.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统日志记录实体
 *
 * @Author: yeting
 * @Date: 2019/5/24 19:44
 */
@Data
@Entity
@Table(name = "sys_log")
public class SysLog extends SuperEntity {
    /**
     * 用户名
     */
    private String username;

    /**
     * 操作
     */
    private String operation;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    private String params;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 操作时间
     */
    private Date createDate;

}
