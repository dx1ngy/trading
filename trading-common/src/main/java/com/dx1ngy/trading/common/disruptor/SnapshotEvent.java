package com.dx1ngy.trading.common.disruptor;

import lombok.Data;

@Data
public class SnapshotEvent {
    private Long lastOffset;
    private String json;
}
