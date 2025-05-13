package com.dx1ngy.trading.engine.manager;

import java.math.BigDecimal;

public record OrderKey(Long orderId, BigDecimal price) {
}
