package com.dx1ngy.core.exception;

import com.dx1ngy.core.ResultCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final Integer code;
    private final Object data;

    public BaseException(ResultCode code) {
        super(code.getMessage());
        this.code = code.getCode();
        this.data = null;
    }

    public BaseException(ResultCode code, Object data) {
        super(code.getMessage());
        this.code = code.getCode();
        this.data = data;
    }

    public BaseException(Integer code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }
}
