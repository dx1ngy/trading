package com.dx1ngy.trading.common.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderBook {
    private BigDecimal price;
    private BigDecimal num;
    private BigDecimal total;
}
