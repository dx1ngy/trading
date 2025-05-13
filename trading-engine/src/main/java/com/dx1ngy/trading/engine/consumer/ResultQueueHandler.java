package com.dx1ngy.trading.engine.consumer;

import com.dx1ngy.core.Result;
import com.dx1ngy.trading.common.disruptor.ResultEvent;
import com.dx1ngy.trading.common.disruptor.ResultEventFactory;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Slf4j
@Component
public class ResultQueueHandler implements EventHandler<ResultEvent> {

    private final Disruptor<ResultEvent> disruptor = new Disruptor<>(new ResultEventFactory(), 65536, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());

    private RingBuffer<ResultEvent> ringBuffer;

    private final KafkaTemplate<String, Result> kafkaTemplate;

    public ResultQueueHandler(KafkaTemplate<String, Result> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
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

    public void publish(Result result) {
        long sequence = ringBuffer.next();
        var resultEvent = ringBuffer.get(sequence);
        resultEvent.setResult(result);
        ringBuffer.publish(sequence);
    }

    @Override
    public void onEvent(ResultEvent event, long sequence, boolean endOfBatch) {
        kafkaTemplate.send("response", event.getResult());
    }
}
