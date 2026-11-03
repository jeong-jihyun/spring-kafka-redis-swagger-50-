package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {
    @Bean
    NewTopic demoEventsTopic() {
        // 로컬 학습용 토픽: partition 3개로 key 기반 분산/병렬 소비를 실습한다.
        // replicas=1은 단일 브로커 개발 환경에 맞춘 값이며 운영에서는 복제 계수를 높여야 한다.
        return TopicBuilder.name("demo.events").partitions(3).replicas(1).build();
    }
}
