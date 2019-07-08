package com.jty.performance.support;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

/**
 * API统一返回结果
 *
 * @author Jason
 * @since 2018/12/17 13:18
 */
@Data
public class Result<T> {

    interface ResultInterface{}

    @JsonView(ResultInterface.class)
    private Integer code;

    @JsonView(ResultInterface.class)
    private String msg;

    @JsonView(ResultInterface.class)
    private T data;

    public Result() {}

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result success() {
        Result result = new Result();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    public static<T> Result<T> success(Object data) {
        Result result = new Result();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public static Result failure(ResultCode resultCode) {
        Result result = new Result();
        result.setResultCode(resultCode);
        return result;
    }

    public static Result failure(Integer code,String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static Result failure(ResultCode resultCode, Object data) {
        Result result = new Result();
        result.setResultCode(resultCode);
        result.setData(data);
        return result;
    }

    public void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }
}
