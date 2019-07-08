package com.jty.performance.exception;

import lombok.Getter;

/**
 * @author Zhongwentao
 * @since 2019/4/3 16:28
 */
@Getter
public class CustomException extends RuntimeException {
    private Integer code;
    private String message;
    public CustomException(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
