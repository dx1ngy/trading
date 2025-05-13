package com.dx1ngy.trading.api;

import lombok.Getter;

@Getter
public enum TradingApiCode {
    ERR_4(4, "数据错误"),
    ;

    private final Integer code;
    private final String message;

    TradingApiCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
