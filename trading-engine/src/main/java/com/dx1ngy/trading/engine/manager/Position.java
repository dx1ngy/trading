package com.dx1ngy.trading.engine.manager;

import com.dx1ngy.trading.common.bean.Goods;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
public class Position {
    private BigDecimal num;
    private ConcurrentSkipListMap<GoodsKey, Goods> goodsMap;
}
