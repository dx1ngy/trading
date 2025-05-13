package com.dx1ngy.trading.engine;

import lombok.Getter;

@Getter
public enum TradingEngineCode {
    ERR_4(4, "数据错误"),
    ERR_5(5, "可用余额不足"),
    ERR_6(6, "取消失败，订单数量不足"),
    ERR_7(7, "取消失败，订单不存在"),
    ERR_8(8, "可平持仓数量不足"),
    ;

    private final Integer code;
    private final String message;

    TradingEngineCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
