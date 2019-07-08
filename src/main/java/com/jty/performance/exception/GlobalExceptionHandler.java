package com.jty.performance.exception;

import com.jty.performance.support.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * TODO
 *
 * @author Jason
 * @since 2018/12/18 13:59
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义异常
     * @param ex
     * @return //
     */
    @ExceptionHandler({MissingServletRequestParameterException.class,BusinessException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleRuntimeException(BusinessException ex) {
        //logger.info("catch BusinessException:"+ ex.toString());
        //不适合查询问题所在位置，所以必须打出所有堆栈
        ex.printStackTrace();
        return Result.failure(ex.getCode());
    }

    /**
     * 处理自定义异常
     * @param ex
     * @return //
     */
    @ExceptionHandler({CustomException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleCustomException(CustomException ex) {
        //logger.info("catch BusinessException:"+ ex.toString());
        //不适合查询问题所在位置，所以必须打出所有堆栈
        ex.printStackTrace();
        return Result.failure(ex.getCode(),ex.getMessage());
    }

//    @ExceptionHandler(value= NestedServletException.class)
//    public Result handleNestedServletException(NestedServletException ex) {
//        logger.info("catch BusinessException:"+ ex.toString());
//        if(ex.getRootCause().getClass().isInstance(BusinessException.class)){
//            Result.failure(((BusinessException)ex.getRootCause()).getCode());
//        }
//        return Result.failure(ResultCode.INTERFACE_ADDRESS_INVALID);
//    }
}
