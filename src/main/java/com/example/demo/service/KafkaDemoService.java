package com.example.demo.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaDemoService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaDemoService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String key, String message) {
        kafkaTemplate.send("demo.events", key, message);
    }
}
