package com.dx1ngy.trading.common.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Order {
    private Long orderId;
    private Long userId;
    private Long goodsId;
    private Integer type;
    //保证金
    private BigDecimal margin;
    private BigDecimal fee;
    private BigDecimal price;
    private BigDecimal num;
    private BigDecimal availableNum;
    private LocalDateTime createTime;
    //多仓或者空仓当时的成交价格
    private CopyOnWriteArrayList<Goods> goodsList;
}
