package com.example.subscribe_commit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.example.GlobalConstant.*;

public class HowToCheckAssignedInfo {

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());



        //auto commit 해제
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);) {
            consumer.subscribe(List.of(TOPIC_NAME));

            Set<TopicPartition> assignment = consumer.assignment(); // 배정 정보(토픽, 파티션)
            assignment.forEach(patition -> System.out.println(patition.topic() + " " + patition.partition()));

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            //...

        }
    }
}
