package com.dx1ngy.trading.engine.manager;

import com.dx1ngy.core.utils.JsonUtil;
import com.dx1ngy.trading.common.bean.Message;
import com.dx1ngy.trading.common.bean.Snapshot;
import com.dx1ngy.trading.engine.TradingEngineProperties;
import com.dx1ngy.trading.engine.consumer.SnapshotQueueHandler;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;


@Slf4j
@Component
public class TradingEngine {

    @Getter
    private volatile long lastOffset = -1;
    @Getter
    private volatile long count = 0;

    private final MessageManager messageManager;
    private final UserManager userManager;
    private final OrderManager orderManager;
    private final MatchEngineManager matchEngineManager;
    private final TradingEngineProperties tradingEngineProperties;
    private final SnapshotQueueHandler snapshotQueueHandler;

    public TradingEngine(MessageManager messageManager,
                         UserManager userManager, OrderManager orderManager,
                         MatchEngineManager matchEngineManager,
                         TradingEngineProperties tradingEngineProperties,
                         SnapshotQueueHandler snapshotQueueHandler) {
        this.messageManager = messageManager;
        this.userManager = userManager;
        this.orderManager = orderManager;
        this.matchEngineManager = matchEngineManager;
        this.tradingEngineProperties = tradingEngineProperties;
        this.snapshotQueueHandler = snapshotQueueHandler;
    }

    @PostConstruct
    public void init() {
        //加载内存快照
        load();
    }

    @KafkaListener(id = "c1", groupId = "request-g1",
            topicPartitions = @TopicPartition(topic = "request",
                    partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "${trading-engine.init-offset}")))
    public void listen(ConsumerRecords<String, Message> records, Acknowledgment ack) {
        try {
            records.forEach(record -> {
                long offset = record.offset();
                if (offset > lastOffset) {
                    messageManager.dispatch(record.value());
                    lastOffset = offset;
                    count++;
                }
            });
            if (count >= 50000) {
                //保存内存快照
                save();
                count = 0;
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("引擎异常,程序退出", e);
            System.exit(1);
        }
    }

    private void save() {
        var snapshot = new Snapshot();
        snapshot.setLastOffset(lastOffset);
        snapshot.setDealId(MatchEngine.getDealId());
        snapshot.setOrderId(messageManager.getOrderId());
        snapshot.setOrderMap(orderManager.getOrderMap());
        snapshot.setGoodsPriceMap(orderManager.getGoodsPriceMap());
        snapshot.setUserMap(userManager.getUserMap());
        snapshot.setUserPositonMap(userManager.getUserPositonMap());
        snapshotQueueHandler.publish(lastOffset, JsonUtil.toJson(snapshot));
    }

    private void load() {
        var directory = Paths.get(tradingEngineProperties.getSnapshotPath());
        try (var fileStream = Files.list(directory)) {
            var lastFile = fileStream.filter(path -> path.toString().endsWith(".json"))
                    .max(Comparator.comparing(path -> {
                        try {
                            // 获取文件的创建时间
                            return Files.readAttributes(path, BasicFileAttributes.class).creationTime();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }));
            if (lastFile.isEmpty()) {
                return;
            }
            var path = lastFile.get();
            String snapshotJson = Files.readString(path, StandardCharsets.UTF_8);
            Snapshot snapshot = JsonUtil.fromJson(snapshotJson, Snapshot.class);
            lastOffset = snapshot.getLastOffset();
            MatchEngine.setDealId(snapshot.getDealId());
            messageManager.setOrderId(snapshot.getOrderId());
            orderManager.setOrderMap(snapshot.getOrderMap());
            orderManager.setGoodsPriceMap(snapshot.getGoodsPriceMap());
            userManager.setUserMap(snapshot.getUserMap());
            userManager.setUserPositonMap(snapshot.getUserPositonMap());
            orderManager.getOrderMap().forEach((k, v) -> {
                userManager.addOrder(v);
                matchEngineManager.process(v);
            });
            log.info("加载内存快照成功={}", path);
        } catch (Exception e) {
            log.error("加载内存快照失败,程序退出", e);
            System.exit(1);
        }
    }

}
