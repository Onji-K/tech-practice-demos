package com.example.dsl;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

public class KStreamJoinKTable {


    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, String> addressTable = builder.table(Constant.ADDRESS_TABLE);
        KStream<String, String> orderStream = builder.stream(Constant.ORDER_STREAM);

        orderStream.join(addressTable,
                (order, address) -> order + " send to " + address)
                .to(Constant.ORDER_JOIN_STREAM);

        KafkaStreams streams = new KafkaStreams(builder.build(), Constant.props);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));


    }

}
