package com.example.dsl;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;

public class SimpleStreamApplication {
    private static String APPLICATION_NAME = "streams-application";
    private static String BOOTSTRAP_SERVERS = "localhost:9092";
    private static String STREAM_LOG = "stream_log";
    private static String STREAM_LOG_COPY = "stream_log_copy";

    public static void main(String[] args) throws InterruptedException {

        StreamsBuilder builder = new StreamsBuilder();
        builder
                .stream(STREAM_LOG)
                .to(STREAM_LOG_COPY);

        KafkaStreams streams = new KafkaStreams(builder.build(), Constant.props);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
