package com.example.processor_api.simple;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import static com.example.processor_api.simple.Constant.*;

public class SimpleKafkaProcessor {

    public static void main(String[] args) {
        Topology topology = new Topology();
        topology.addSource("Source", STREAM_LOG)
                .addProcessor("Process", FilterProcessor::new, "Source")
                .addSink("Sink", STREAM_LOG_FILTER, "Process");

        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }
}
