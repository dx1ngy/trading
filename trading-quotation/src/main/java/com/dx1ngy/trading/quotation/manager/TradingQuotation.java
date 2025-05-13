package com.dx1ngy.trading.quotation.manager;

import com.dx1ngy.core.Results;
import com.dx1ngy.trading.common.bean.Data99;
import com.dx1ngy.trading.common.bean.Deal;
import com.dx1ngy.trading.common.bean.Message;
import com.dx1ngy.trading.quotation.consumer.ResultQueueHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TradingQuotation {

    private volatile long lastDealId = 0;

    private final QuotationManager quotationManager;
    private final ResultQueueHandler resultQueueHandler;

    public TradingQuotation(QuotationManager quotationManager,
                            ResultQueueHandler resultQueueHandler) {
        this.quotationManager = quotationManager;
        this.resultQueueHandler = resultQueueHandler;
    }

    @KafkaListener(id = "c1", groupId = "deal-g1",
            topicPartitions = @TopicPartition(topic = "deal",
                    partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "${trading-quotation.init-offset}")))
    public void listen(ConsumerRecords<String, Deal> records, Acknowledgment ack) {
        try {
            records.forEach(record -> {
                var deal = record.value();
                if (lastDealId < deal.getDealId() && !deal.getBuyOrder().getUserId().equals(deal.getSellOrder().getUserId())) {
                    quotationManager.addDeal(deal);
                    var tick1 = quotationManager.addTick(deal, 1L);
                    var tick60 = quotationManager.addTick(deal, 60L);
                    var tick3600 = quotationManager.addTick(deal, 3600L);
                    var tick86400 = quotationManager.addTick(deal, 86400L);
                    lastDealId = deal.getDealId();
                    var data99 = new Data99();
                    data99.setDealId(deal.getDealId());
                    data99.setGoodsId(deal.getBuyOrder().getGoodsId());
                    data99.setBuyUserId(deal.getBuyOrder().getUserId());
                    data99.setSellUserId(deal.getSellOrder().getUserId());
                    data99.setTick1(tick1);
                    data99.setTick60(tick60);
                    data99.setTick3600(tick3600);
                    data99.setTick86400(tick86400);
                    var message = new Message();
                    message.setMessageType(99);
                    message.setData(data99);
                    resultQueueHandler.publish(Results.success(message));
                }
            });
            ack.acknowledge();
        } catch (Exception e) {
            log.error("行情异常,程序退出", e);
            System.exit(1);
        }
    }
}
