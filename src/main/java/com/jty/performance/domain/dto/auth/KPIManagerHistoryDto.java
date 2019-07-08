package com.jty.performance.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jty.performance.domain.dto.SuperDto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Manjiajie
 * @since 2019-4-29 10:04:30
 */
@Data
public class KPIManagerHistoryDto extends SuperDto {
    /**
     * 操作人的名字
     */
    private String operatorName;
    /**
     * 操作项
     */
    private String operation;
    /**
     * 操作原因
     */
    private String operateReason;
    /**
     * 排序
     */
    private Integer ordinal;
    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedTime;
}
