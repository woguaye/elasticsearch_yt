package com.jty.performance.exception;

import com.jty.performance.support.ResultCode;
import lombok.Data;


/**
 * 统一抛出异常
 *
 * @author Jason
 * @since 2018/12/18 10:31
 */
@Data
public class BusinessException extends RuntimeException{

    private ResultCode code;

    public BusinessException(ResultCode code){
        this.code=code;
    }

    /**
     * 处理动态传参
     * @param code
     * @param message
     */
    public BusinessException(ResultCode code, String... message){
        String meg=code.message();
        code.setMessage(String.format(meg,message));
        this.code=code;
    }
}
