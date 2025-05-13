package com.dx1ngy.trading.common.disruptor;

import com.lmax.disruptor.EventFactory;

public class DealEventFactory implements EventFactory<DealEvent> {
    @Override
    public DealEvent newInstance() {
        return new DealEvent();
    }
}
