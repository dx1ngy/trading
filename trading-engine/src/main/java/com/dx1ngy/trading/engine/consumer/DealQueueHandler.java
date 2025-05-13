package com.dx1ngy.trading.engine.consumer;

import com.dx1ngy.trading.common.bean.Deal;
import com.dx1ngy.trading.common.disruptor.DealEvent;
import com.dx1ngy.trading.common.disruptor.DealEventFactory;
import com.dx1ngy.trading.engine.manager.UserManager;
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

import java.util.Objects;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class DealQueueHandler implements EventHandler<DealEvent> {

    private final Disruptor<DealEvent> disruptor = new Disruptor<>(new DealEventFactory(), 65536, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());

    private RingBuffer<DealEvent> ringBuffer;

    private final UserManager userManager;
    private final KafkaTemplate<String, Deal> kafkaTemplate;

    public DealQueueHandler(UserManager userManager,
                            KafkaTemplate<String, Deal> kafkaTemplate) {
        this.userManager = userManager;
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

    public void publish(Deal deal) {
        long sequence = ringBuffer.next();
        var dealEvent = ringBuffer.get(sequence);
        dealEvent.setDeal(deal);
        ringBuffer.publish(sequence);
    }

    @Override
    public void onEvent(DealEvent event, long sequence, boolean endOfBatch) {
        var deal = event.getDeal();
        if (!Objects.equals(deal.getBuyOrder().getUserId(), deal.getSellOrder().getUserId())) {
            //更新持仓保证金和浮动盈亏
            userManager.getUserMap().forEach((userId, user) ->
                    userManager.updateMarginGainLoss(userId, deal.getBuyOrder().getGoodsId(), deal.getDealPrice()));
        }
        kafkaTemplate.send("deal", deal);
    }

}
