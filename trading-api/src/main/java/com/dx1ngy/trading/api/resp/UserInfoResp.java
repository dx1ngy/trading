package com.dx1ngy.trading.api.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserInfoResp {
    private BigDecimal money;
    private BigDecimal margin;
    private BigDecimal gainLoss;
    private String safetyRate;
    private BigDecimal longNum;
    private BigDecimal shortNum;
}
