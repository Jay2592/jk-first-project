package com.example.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    @KafkaListener(topics = "sample-topic", groupId = "sample-group")
    public void onMessage(String message) {
        System.out.println("[Kafka] Received: " + message);
        // Add processing logic, e.g., send to batch or DB
    }
}
