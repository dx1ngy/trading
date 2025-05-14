package com.dx1ngy.trading.common.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class User {
    private Long userId;
    //可用余额
    private BigDecimal num;
    //持仓保证金 goodsId:price
    private ConcurrentHashMap<Long, BigDecimal> marginMap;
    //浮动盈亏 goodsId:price
    private ConcurrentHashMap<Long, BigDecimal> gainLossMap;
}
