package com.example.basic_prod_consume.consumer;

import com.example.GlobalConstant;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class SimpleConsumer{
    private final static Logger logger = LoggerFactory.getLogger(SimpleConsumer.class.getName());
    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = GlobalConstant.BOOTSTRAP_SERVERS;
    /*
    컨슈머 그룹 아이디를 지정하자, 컨슈머 그룹 아이디를 기준으로 오프셋을 관리한다.
     */
    private final static String GROUP_ID = "test-group";


    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs)){
            consumer.subscribe(List.of(TOPIC_NAME)); //컨슈머에게 토픽을 할당한다. 하나 이상 할당이 가능하다.

            //계속해서 poll을 하도록 만든다.
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1)); // 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임 아웃 간격
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("record info {}", record);
                }
            }
        }


    }
}
