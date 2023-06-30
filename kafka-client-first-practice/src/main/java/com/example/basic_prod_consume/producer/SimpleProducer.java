package com.example.basic_prod_consume.producer;

import com.example.GlobalConstant;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SimpleProducer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = GlobalConstant.BOOTSTRAP_SERVERS;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties configs = new Properties(); // 프로듀서를 생성하기 위한 설정 정보
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // 직렬화 도구 - 카프카 라이브러리의 StringSerializer 클래스를 사용
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,CustomPartitioner.class); //커스텀 파티셔너 사용

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String messageValue = "testMessage";
        String messageKey = "Pangyo";
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME,messageKey, messageValue); //String topic, V value -> 이 경우 key는 null 로 전송된다.
        logger.info("record info : {}",record);
        /*
        send 의 경우 즉시 전송하는것이 아니라 레코드를 묶어서 보낸다 -> 배치 전송
        리턴 값으로 Future<RecordMetadata> 를 반환한다.
        카프카 브로커로 부터 응답을 받으면 RecordMetadata 객체를 반환한다.
         */
        Future<RecordMetadata> recordMetadataFuture = producer.send(record);
        RecordMetadata recordMetadata = recordMetadataFuture.get();
        logger.info("recordMetadata info : {}",recordMetadata.toString());
        /*
        하지만 이 경우 성능상 문제가 발생할 수 있다. 응답을 받을 떄까지 동기적으로 대기하는 것은 비효율적이다.
        따라서 콜백 메서드를 활용하여 비동기적으로 응답을 처리하는 것이 좋다.
         */
        producer.send(record, new ProducerCallback()); // 비동기로 로그 처리
        producer.flush();
        producer.close(); // close를 호출하여 producer 인스턴스 내부의 리소스를 안전하게 종료한다.
    }
}
