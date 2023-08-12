package com.example.subscribe_commit;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

import static com.example.GlobalConstant.*;

public class ReactToRebalance {

    private static Logger logger = LoggerFactory.getLogger(ReactToRebalance.class.getName());
    private static KafkaConsumer<String,String> consumer;
    private static Map<TopicPartition, OffsetAndMetadata> currentOffsets;

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try {
            consumer = new KafkaConsumer<>(configs);
            consumer.subscribe(List.of(TOPIC_NAME), new RebalanceListener()); //여기에 리밸런스 리스너를 넣어 준다.

            currentOffsets = new HashMap<>();

            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String,String> record : records){
                    logger.info("{}",record);
                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1, null)
                            //이렇게 +1을 해줌으로서 컨슈머 재시작시 파티션에서 가장 마지막 값을 기준으로 레코드를 읽기 시작한다.
                    );
                    consumer.commitSync(currentOffsets);
                    /*
                    컨슈머가 리밸런싱 동안에는 데이터를 읽지 못한다.
                    따라서 컨슈머 재 시작시
                     */
                }
            }

        } finally {
            consumer.close();
        }

    }

    private static class RebalanceListener implements ConsumerRebalanceListener {

        //리밸런싱이 이루어지기 직전에 호출된다.
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            logger.warn("Partitions are revoked");
            //리밸런싱이 이루어 지기 적전에 커밋을 해준다.
            consumer.commitSync(currentOffsets);
            currentOffsets.clear();
        }

        /*
        리밸런싱이 진행되는 동안에는 컨슈머 그룹 내의 컨슈머 들이 토픽의 데이터를 읽지 못한다.
         */

        //리밸런싱이 완료된 후에 호출된다.
        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            logger.warn("Partitions are assigned");
        }
    }
}

