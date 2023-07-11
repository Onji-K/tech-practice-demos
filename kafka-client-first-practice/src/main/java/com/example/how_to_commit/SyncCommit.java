package com.example.how_to_commit;

import com.example.GlobalConstant;
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

public class SyncCommit {

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

            //동기 커밋 - 모든 레코드를 다 처리한 후 커밋해야한다.
            consumer.commitSync();
            /*
            이것이 없으면, auto-commit이 false이기 때문에, 항상 처음 부터 불러와진다.
            오프셋 커밋이 되어있지 않으므로
             */

        }





    }
}
