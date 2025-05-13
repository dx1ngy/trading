package com.dx1ngy.trading.common.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Tick {
    private Long timestamp;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal volume;
    private BigDecimal turnover;
}
