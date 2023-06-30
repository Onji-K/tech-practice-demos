package com.example.basic_prod_consume.producer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

//예시
public class CustomPartitioner implements Partitioner {
    // 파티션을 결정하는 로직을 구현
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // key 가 전달되지 않으면 InvalidRecordException 예외를 발생시킨다.
        if (keyBytes == null) {
            throw new InvalidRecordException("Need message key");
        }

        // 키가 판교면 0번 파티션에 배정해라
        if (((String)key).equals("Pangyo")){
            return 0;
        }

        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic); // 토픽에 대한 파티션 정보를 조회한다.
        int numPartitions = partitions.size();
        return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions; // 키 바이트로 부터 해시값을 구한 뒤 파티션 개수로 나눈 나머지를 반환한다.
    }

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}
}
