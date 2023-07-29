package com.example.how_to_commit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.example.GlobalConstant.*;

/*
<h1>.assign()</h1>
assign은 subscribe와 다르게 토픽과 파티션을 지정하여 구독한다.<br>
덕분에 리밸런싱이 발생하지 않는다.
 */
public class AssignTopic {

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());


        final int PARTITION_NUMBER = 0;

        //auto commit 해제
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);) {
            consumer.assign(Collections.singleton(new TopicPartition(TOPIC_NAME, PARTITION_NUMBER))); // 이부분이 다르다

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            //...

        }
    }

}
