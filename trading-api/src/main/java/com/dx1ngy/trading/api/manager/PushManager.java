package com.dx1ngy.trading.api.manager;

import com.dx1ngy.core.Result;
import com.dx1ngy.core.utils.JsonUtil;
import com.dx1ngy.trading.common.bean.Data0_2;
import com.dx1ngy.trading.common.bean.Data3_7;
import com.dx1ngy.trading.common.bean.Data99;
import com.dx1ngy.trading.common.bean.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushManager {

    @KafkaListener(id = "c1", groupId = "response-g1",
            topicPartitions = @TopicPartition(topic = "response", partitions = {"0"}))
    public void listen(ConsumerRecords<String, Result> records, Acknowledgment ack) {
        try {
            records.forEach(record -> {
                Result result = record.value();
                Message message = (Message) result.getData();
                Object data = message.getData();
                if (data instanceof Data0_2 data0_2) {//不管成功还是失败都给指定人推
                    message.setData(null);
                    WebSocketManager.sendMessage(data0_2.getUserId(), JsonUtil.toJson(result));
                } else if (data instanceof Data3_7 data3_7) {//如果失败给指定人推 成功给所有人推
                    Long userId = data3_7.getUserId();
                    Long goodsId = data3_7.getGoodsId();
                    data3_7 = new Data3_7();
                    data3_7.setUserId(userId);
                    data3_7.setGoodsId(goodsId);
                    message.setData(data3_7);
                    if (result.getCode() == 0) {//成功
                        WebSocketManager.sendMessage(JsonUtil.toJson(result));
                    } else {//失败
                        WebSocketManager.sendMessage(userId, JsonUtil.toJson(result));
                    }
                } else if (data instanceof Data99) {//k线数据 给所有人推
                    WebSocketManager.sendMessage(JsonUtil.toJson(result));
                }
            });
            ack.acknowledge();
        } catch (Exception e) {
            log.error("推送失败", e);
        }
    }
}
