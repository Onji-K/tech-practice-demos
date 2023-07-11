package com.example.how_to_commit;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.example.GlobalConstant.*;

public class SyncOffsetCommit {

    private final static Logger logger = LoggerFactory.getLogger(SyncCommit.class.getName());

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());


        //가장 처음부터 읽어오기
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        //auto commit 해제
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);) {
            consumer.subscribe(List.of(TOPIC_NAME));

            //현재 오프셋을 나타낼 맵
            Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String,String> record : records) {
                logger.info("record info -> {} : {}" , record.key(), record.value());

                //현재 오프셋을 맵에 저장
                currentOffset.put(
                        new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() +1 , null)
                        /*
                        여기서 중요한 점은 오프셋에 +1을 해줘야한다는 점이다.
                        그 이유는 컨슈머가 poll을 할 때 마지막으로 커밋한 오프셋 부터 리턴을 하기 때문이다.
                        약간 tcp 통신에서 ack를 보내는 것과 비슷한 느낌이다.
                         */
                );

                //이 부분까지만 커밋한다.
                consumer.commitSync(currentOffset);
            }

            //동기 커밋 - 모든 레코드를 다 처리한 후 커밋해야한다.
            consumer.commitSync();


        }





    }
}
