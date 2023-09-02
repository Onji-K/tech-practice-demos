package com.example.safe_consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.example.GlobalConstant.*;
import static com.example.KafkaTestUtil.*;

/*
내가 생각하는 안전한 컨슈머 예제
 */
public class ConsumerExample {

    private final static Logger logger = Logger.getLogger(ConsumerExample.class.getName());

    private final static int COMMIT_RECORD_COUNT_THRESHOLD = 5; //몇개의 레코드를 처리한 후 커밋할 것인지
    private final static Duration COMMIT_TIME_THRESHOLD = Duration.ofSeconds(10); //커밋할 시간 간격
    private final static HashMap<TopicPartition, Instant> commitTimeRecorder = new HashMap<>();


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
                    for (TopicPartition partition : records.partitions()) {
                        List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                        //소비
                        partitionRecords.forEach(record -> logger.info(String.format("record info -> {} : {}", record.key(), record.value())));
                        //커밋할 조건인지 체크
                        if (isPartitionNeedCommit(partition, consumer)) {
                            //파티션 별로 처리된 오프셋을 커밋한다.
                            long lastConsumedOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                            consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastConsumedOffset + 1)));
                            //시간 기록
                            commitTimeRecorder.put(partition, Instant.now());
                        }
                    }
                } catch (Exception e) {
                    //TODO : java doc 을 참고하여 예외처리
                }
            }

        }
    }
    private static boolean isPartitionNeedCommit(TopicPartition partition, KafkaConsumer<String,String> consumer){
        boolean countOver = consumer.position(partition) - consumer.committed(partition).offset() > COMMIT_RECORD_COUNT_THRESHOLD;
        Optional<Instant> committedTime = Optional.ofNullable(commitTimeRecorder.get(partition));
        boolean timeOver = committedTime.isEmpty() || Duration.between(committedTime.get(), Instant.now()).compareTo(COMMIT_TIME_THRESHOLD) > 0;
        return countOver || timeOver;
    }


    public static class CommitOffsetOnRebalance implements ConsumerRebalanceListener {

        private final static Logger logger = Logger.getLogger(CommitOffsetOnRebalance.class.getName());

        private final KafkaConsumer<String,String> consumer;

        public CommitOffsetOnRebalance(KafkaConsumer<String, String> consumer) {
            this.consumer = consumer;
        }

        /**
         * 리밸런싱 직전 호출된다.
         * @param partitions 이제 다른 소비자에게 소유권이 넘어갈 파티션들의 목록
         */
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            //컨슈머가 리밸런싱 되기 직전에 호출된다.
            // 리밸런싱이 예정된 파티션들의 마지막 committed offset 을 가져온다.
            Map<TopicPartition, OffsetAndMetadata> committedOffsets = consumer.committed(new HashSet<>(partitions));
            committedOffsets.entrySet().stream()
                    .filter(entry -> isPartitionNeedCommit(entry.getKey(), entry.getValue()))
                    .forEach(entry -> {
                        //client의 position을 커밋한다.
                        long nextFetchTarget = consumer.position(entry.getKey());
                        TopicPartition topicPartition = entry.getKey();
                        consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(nextFetchTarget, null)));
                    });
            //커밋된 시간 기록을 삭제한다.
            partitions.forEach(commitTimeRecorder::remove);
        }

        /**
         * 리밸런싱이 일어난 후 호출된다. (poll 메서드의 일부로서)
         * @param partitions 이제 소비자에게 할당된 파티션 목록(이전에 소유한 파티션은 포함되지 않음, 즉 이 목록에는 새로 추가된 파티션만 포함됨)
         */
        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            String topicPartition = partitions.stream()
                    .map(TopicPartition::toString)
                    .collect(Collectors.joining(",", "[", "]"));
            logger.info("Newly Assigned partitions : " + topicPartition);
        }

        /**
         * 오류로 인해 이미 다른 컨슈머에게 할당되었을 때 호출됨 (poll 메서드의 일부로서)
         * @param partitions 소비자가 소유하고 있던 파티션 목록 (전체 목록)
         */
        @Override
        public void onPartitionsLost(Collection<TopicPartition> partitions) {
            //커밋되지 않는 부분까지 보상 작업을 고려해볼 수 있음(이미 다른 컨슈머에게 넘어간 상태이기 때문에)
            consumer.committed(new HashSet<>(partitions))
                    .entrySet()
                    .stream()
                    .filter(entry -> isPartitionNeedCommit(entry.getKey(), entry.getValue()))
                    .forEach(entry -> rollbackFunction(entry.getKey(), entry.getValue()));
            logger.info("rollback function called");
            partitions.forEach(commitTimeRecorder::remove);
        }

        private void rollbackFunction(TopicPartition topicPartition, OffsetAndMetadata offsetAndMetadata) {
            //TODO: rollback function
        }

        private boolean isPartitionNeedCommit(TopicPartition topicPartition, OffsetAndMetadata offsetAndMetadata) {
            if (offsetAndMetadata == null) {
                return true;
            } else {
                long nextFetchOffset = consumer.position(topicPartition);
                long committedOffset = offsetAndMetadata.offset();
                return nextFetchOffset > committedOffset;
            }
        }
    }


}
