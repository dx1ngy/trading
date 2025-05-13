package com.dx1ngy.trading.common.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Data3_7 {
    private Long orderId;
    private Long userId;
    private Long goodsId;
    private Integer type;
    private BigDecimal margin;
    private BigDecimal fee;
    private BigDecimal price;
    private BigDecimal num;
    private BigDecimal availableNum;
    private LocalDateTime createTime;
}
