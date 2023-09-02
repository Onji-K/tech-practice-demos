package com.example;

public interface GlobalConstant {
    String BOOTSTRAP_SERVERS = System.getenv("BOOTSTRAP_SERVERS");
    String TOPIC_NAME = "test";
    String GROUP_ID = "test-group";
}
