package com.dx1ngy.trading.common.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Deal {
    private Long dealId;
    private BigDecimal dealPrice;
    private BigDecimal dealNum;
    private Order buyOrder;
    private Order sellOrder;
    private LocalDateTime dealTime;
}
