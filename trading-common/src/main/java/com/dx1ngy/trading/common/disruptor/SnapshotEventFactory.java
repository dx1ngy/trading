package com.dx1ngy.trading.common.disruptor;

import com.lmax.disruptor.EventFactory;

public class SnapshotEventFactory implements EventFactory<SnapshotEvent> {
    @Override
    public SnapshotEvent newInstance() {
        return new SnapshotEvent();
    }
}
