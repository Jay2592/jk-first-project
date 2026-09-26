package com.example.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitListenerService {

    @RabbitListener(queues = "sample-queue")
    public void receive(String message) {
        System.out.println("[Rabbit] Received: " + message);
        // Add processing logic here
    }
}
