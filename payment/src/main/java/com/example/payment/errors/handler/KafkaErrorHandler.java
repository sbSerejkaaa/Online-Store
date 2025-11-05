package com.example.payment.errors.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.stereotype.Component;
import org.xml.sax.helpers.DefaultHandler;
/*
import java.util.List;
@Slf4j
@Component
public class KafkaErrorHandler extends DefaultHandler {
    public KafkaErrorHandler(KafkaTemplate<String, String> dlqTemplate) {
        super(
                new DeadLetterPublishingRecoverer(
                        dlqTemplate,
                        (record, ex) -> new TopicPartition(record.topic() + ".dlq", record.partition())
                ),
                new ExponentialBackOffWithMaxRetries(3)// 3 повтора: 1s,2s,4s
        );

        // 1) Не пытаться ретраить, если в процессе обработки упало
        //    именно наше «InsufficientFundsException»
        addNotRetryableExceptions(InsufficientFundsException.class);

        // 2) После recover (т. е. после публикации в DLQ) зафиксировать offset
        setCommitRecovered(true);
    }


    @Override
    public void handleRemaining(Exception thrownException,
                                List<ConsumerRecord<?, ?>> records,
                                Consumer<?, ?> consumer,
                                MessageListenerContainer container) {
        super.handleRemaining(thrownException, records, consumer, container);
    }

}

 */
