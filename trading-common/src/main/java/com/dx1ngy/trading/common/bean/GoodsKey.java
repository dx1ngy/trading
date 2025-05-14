package com.dx1ngy.trading.common.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
public class GoodsKey {
    private final Long timestamp = System.currentTimeMillis();

    private BigDecimal dealPrice;
    private BigDecimal margin;

    public GoodsKey(BigDecimal dealPrice, BigDecimal margin) {
        this.dealPrice = dealPrice;
        this.margin = margin;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GoodsKey goodsKey)) {
            return false;
        }
        return Objects.equals(dealPrice, goodsKey.dealPrice) && Objects.equals(margin, goodsKey.margin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dealPrice, margin);
    }

}
