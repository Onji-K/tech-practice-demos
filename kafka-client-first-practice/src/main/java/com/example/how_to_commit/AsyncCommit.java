package com.example.how_to_commit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static com.example.GlobalConstant.*;

public class AsyncCommit {
    private final static Logger logger = LoggerFactory.getLogger(SyncCommit.class.getName());

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());


//        //가장 처음부터 읽어오기
//        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        //auto commit 해제
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);) {
            consumer.subscribe(List.of(TOPIC_NAME));

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            records.forEach(record -> {
                logger.info("record info -> {} : {}" , record.key(), record.value());
            });

            //비동기 커밋
//            consumer.commitAsync();
            //콜백 지정
            consumer.commitAsync((offsets, exception) -> {
                if (exception != null) {
                    logger.error(exception.getMessage(), exception);
                } else {
                    logger.info(offsets.toString());
                }
            });
        }

    }
}
