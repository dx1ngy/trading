package com.dx1ngy.trading.engine.consumer;

import com.dx1ngy.trading.common.disruptor.SnapshotEvent;
import com.dx1ngy.trading.common.disruptor.SnapshotEventFactory;
import com.dx1ngy.trading.engine.TradingEngineProperties;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class SnapshotQueueHandler implements EventHandler<SnapshotEvent> {

    private final Disruptor<SnapshotEvent> disruptor = new Disruptor<>(new SnapshotEventFactory(), 65536, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());

    private RingBuffer<SnapshotEvent> ringBuffer;

    private final TradingEngineProperties tradingEngineProperties;

    public SnapshotQueueHandler(TradingEngineProperties tradingEngineProperties) {
        this.tradingEngineProperties = tradingEngineProperties;
    }

    @PostConstruct
    public void init() {
        disruptor.handleEventsWith(this);
        ringBuffer = disruptor.start();
    }

    @PreDestroy
    public void destroy() {
        disruptor.shutdown();
    }

    public void publish(Long lastOffset, String json) {
        long sequence = ringBuffer.next();
        var snapshotEvent = ringBuffer.get(sequence);
        snapshotEvent.setLastOffset(lastOffset);
        snapshotEvent.setJson(json);
        ringBuffer.publish(sequence);
    }

    @Override
    public void onEvent(SnapshotEvent event, long sequence, boolean endOfBatch) throws Exception {
        String path = tradingEngineProperties.getSnapshotPath() + "snapshot-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss-" + event.getLastOffset())) + ".json";
        Files.writeString(Paths.get(path), event.getJson(), StandardCharsets.UTF_8);
        log.info("保存内存快照成功={}", path);
    }

}
