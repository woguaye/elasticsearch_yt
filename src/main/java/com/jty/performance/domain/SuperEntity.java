package com.jty.performance.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 实体统一父类
 *
 * @author Jason
 * @since 2018/12/17 11:23
 */
@MappedSuperclass
@Data
public class SuperEntity {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /***
     * 数据创建时间
     */
    @Column(name = "created_time", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间'")
    @CreationTimestamp
    private LocalDateTime createdTime;

    /***
     * 数据修改时间
     */
    @Column(name = "modified_time", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据修改时间'")
    @UpdateTimestamp
    private LocalDateTime modifiedTime;
}
