package com.example.dsl;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

public class StreamsFilter {
    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(Constant.STREAM_LOG);
        stream.filter((key, value) -> value.length() > 5)
                .to(Constant.STREAM_LOG_COPY);

        KafkaStreams streams = new KafkaStreams(builder.build(), Constant.props);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
