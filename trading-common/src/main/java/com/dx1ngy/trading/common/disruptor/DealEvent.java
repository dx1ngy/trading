package com.dx1ngy.trading.common.disruptor;

import com.dx1ngy.trading.common.bean.Deal;
import lombok.Data;

@Data
public class DealEvent {
    private Deal deal;
}
