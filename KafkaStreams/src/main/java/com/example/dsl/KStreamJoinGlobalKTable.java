package com.example.dsl;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;

public class KStreamJoinGlobalKTable {


    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        GlobalKTable<String, String> addressGlobalTable = builder.globalTable(Constant.ADDRESS_GLOBAL_TABLE);
        KStream<String, String> orderStream = builder.stream(Constant.ORDER_STREAM);

        orderStream.join(addressGlobalTable,
                (orderKey, orderValue) -> orderKey,
                (order, address) -> order + " send to " + address)
                .to(Constant.ORDER_JOIN_STREAM);

        KafkaStreams streams = new KafkaStreams(builder.build(), Constant.props);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
