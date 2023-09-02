package com.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

import static com.example.GlobalConstant.BOOTSTRAP_SERVERS;
import static com.example.GlobalConstant.GROUP_ID;

public class KafkaTestUtil {

    public static KafkaConsumer<String,String> createConsumer(boolean autoCommit, boolean readFromBeginning){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());


        //가장 처음부터 읽어오기
        if (readFromBeginning) {
            configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        }

        //auto commit 해제
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);

        return new KafkaConsumer<>(configs);
    }
}
