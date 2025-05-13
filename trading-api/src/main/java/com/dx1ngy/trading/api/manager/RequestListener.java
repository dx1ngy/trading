package com.dx1ngy.trading.api.manager;

import com.dx1ngy.trading.common.bean.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class RequestListener {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    public RequestListener(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionSuccessEvent(RequestEvent event) {
        kafkaTemplate.send("request", event.message());
    }

}
