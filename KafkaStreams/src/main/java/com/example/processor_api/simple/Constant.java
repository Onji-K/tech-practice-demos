package com.example.processor_api.simple;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class Constant {
    public static final Properties props;
    public static String APPLICATION_NAME = "processor-application";
    public static String BOOTSTRAP_SERVERS = "localhost:9092";
    public static String STREAM_LOG = "stream_log";
    public static String STREAM_LOG_FILTER = "stream_log_filter";

    static {
        props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }
}
