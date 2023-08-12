package com.example.safe_consumer;

import com.example.GlobalConstant;
import com.example.KafkaTestUtil;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.example.GlobalConstant.*;
import static com.example.KafkaTestUtil.*;

/*
내가 생각하는 안전한 컨슈머 예제
 */
public class ConsumerExample {

    public static class ReBalancedException extends RuntimeException{
        public ReBalancedException(String message) {
            super(message);
        }
    }

    public static class CommitOffsetOnRebalance implements ConsumerRebalanceListener {

        private final Consumer<?,?> consumer;

        public CommitOffsetOnRebalance(Consumer<?, ?> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            //revoke 가 예정된 파티션 들만, poll()한 곳까지 커밋한다.
            for (TopicPartition partition : partitions) {
                OffsetAndMetadata offsetAndMetadata = consumer.poll()
                if (offsetAndMetadata != null) {
                    consumer.commitSync(Map.of(partition, offsetAndMetadata));
                }
            }



        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

        }

        @Override
        public void onPartitionsLost(Collection<TopicPartition> partitions) {
            ConsumerRebalanceListener.super.onPartitionsLost(partitions);
        }
    }

    public static void main(String[] args) {
        boolean autoCommit = false;
        boolean readFromBeginning = false;

        try (KafkaConsumer<String, String> consumer = createConsumer(autoCommit,readFromBeginning)){
            //프로그램 종료시 컨슈머를 종료시키기 위한 훅을 등록한다.
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            //컨슈머에게 토픽을 구독하고 리밸런싱 리스너를 등록한다.
            consumer.subscribe(List.of(TOPIC_NAME), new CommitOffsetOnRebalance(consumer));

            while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, String> record : records) {
                        record.partition()
                        System.out.printf("key = %s, value = %s%n", record.key(), record.value());

                    }

                } catch (ReBalancedException e){
                    //리밸런스 되었으므로, 커밋 되지 않은 오프셋을 커밋한다.
                }
            }

        }
    }


}
