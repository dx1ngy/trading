package com.dx1ngy.trading.common.bean;

import java.math.BigDecimal;

public record OrderKey(Long orderId, BigDecimal price) {
}
