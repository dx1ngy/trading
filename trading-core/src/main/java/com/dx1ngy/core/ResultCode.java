package com.dx1ngy.core;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(0, "操作成功"),
    ERROR(1, "系统异常"),
    PARAM_ERROR(2, "参数错误"),
    TOKEN_ERROR(3, "token无效"),
    ;

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
