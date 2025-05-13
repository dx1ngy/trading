package com.dx1ngy.trading.quotation;


import com.dx1ngy.core.exception.BaseException;

public class TradingQuotationException extends BaseException {

    public TradingQuotationException(TradingQuotationCode code) {
        super(code.getCode(), code.getMessage(), null);
    }

    public TradingQuotationException(TradingQuotationCode code, Object data) {
        super(code.getCode(), code.getMessage(), data);
    }

    public TradingQuotationException(Integer code, String message, Object data) {
        super(code, message, data);
    }

}
