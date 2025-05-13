package com.dx1ngy.trading.quotation;

import lombok.Getter;

@Getter
public enum TradingQuotationCode {
    ERR_4(4, "数据错误"),
    ;

    private final Integer code;
    private final String message;

    TradingQuotationCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
