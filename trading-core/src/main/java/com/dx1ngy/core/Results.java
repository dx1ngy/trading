package com.dx1ngy.core;


import com.dx1ngy.core.exception.BaseException;

public class Results {

    public static Result success(Object data) {
        var result = new Result();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static Result error(Integer code, String message, Object data) {
        var result = new Result();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static Result success() {
        return success(null);
    }

    public static Result error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static Result error(BaseException e) {
        return error(e.getCode(), e.getMessage(), e.getData());
    }

}
