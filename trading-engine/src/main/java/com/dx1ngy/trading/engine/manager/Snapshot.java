package com.dx1ngy.trading.engine.manager;

import com.dx1ngy.trading.common.bean.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Snapshot {
    private Long lastOffset;
    private Long dealId;
    private Long orderId;
    private ConcurrentHashMap<Long, Order> orderMap;
    private ConcurrentHashMap<Long, BigDecimal> goodsPriceMap;
    private ConcurrentHashMap<Long, User> userMap;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, ConcurrentHashMap<Integer, Position>>> userPositonMap;
}
