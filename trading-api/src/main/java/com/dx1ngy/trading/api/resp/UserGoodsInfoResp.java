package com.dx1ngy.trading.api.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserGoodsInfoResp {
    private BigDecimal currentPrice;
    private BigDecimal upPrice;
    private BigDecimal downPrice;
    private BigDecimal margin;
    private BigDecimal fee;
}
