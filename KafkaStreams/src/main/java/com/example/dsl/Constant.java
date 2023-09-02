package com.example.dsl;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class Constant {
    public static String APPLICATION_NAME = "streams-application";
    public static String BOOTSTRAP_SERVERS = "localhost:9092";
    public static String STREAM_LOG = "stream_log";
    public static String STREAM_LOG_COPY = "stream_log_copy";
    public static String ADDRESS_TABLE = "address";
    public static String ORDER_STREAM = "order";
    public static String ORDER_JOIN_STREAM = "order_join";
    public static String ADDRESS_GLOBAL_TABLE = "address_v2";
    public static Properties props;

    static {
        props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }

}
