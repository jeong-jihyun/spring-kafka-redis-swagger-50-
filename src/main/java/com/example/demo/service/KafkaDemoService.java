package com.example.demo.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaDemoService {
    // KafkaTemplate은 Spring Kafka가 설정한 producer를 감싸는 전송 진입점이다.
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaDemoService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * key는 partition 선택에 사용되고, message는 record value로 저장된다.
     * send()는 비동기 전송을 시작하므로 호출 직후 broker 저장 완료를 의미하지 않는다.
     * 운영 코드에서는 반환 Future를 확인해 성공 metadata(offset/partition)와 실패를 처리한다.
     */
    public void publish(String key, String message) {
        kafkaTemplate.send("demo.events", key, message);
    }
}
