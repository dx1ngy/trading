package com.dx1ngy.trading.api;


import com.dx1ngy.core.exception.BaseException;

public class TradingApiException extends BaseException {

    public TradingApiException(TradingApiCode code) {
        super(code.getCode(), code.getMessage(), null);
    }

    public TradingApiException(TradingApiCode code, Object data) {
        super(code.getCode(), code.getMessage(), data);
    }

    public TradingApiException(Integer code, String message, Object data) {
        super(code, message, data);
    }

}
