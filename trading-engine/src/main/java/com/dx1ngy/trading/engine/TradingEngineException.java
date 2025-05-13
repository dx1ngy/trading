package com.dx1ngy.trading.engine;


import com.dx1ngy.core.exception.BaseException;

public class TradingEngineException extends BaseException {

    public TradingEngineException(TradingEngineCode code) {
        super(code.getCode(), code.getMessage(), null);
    }

    public TradingEngineException(TradingEngineCode code, Object data) {
        super(code.getCode(), code.getMessage(), data);
    }

    public TradingEngineException(Integer code, String message, Object data) {
        super(code, message, data);
    }

}
