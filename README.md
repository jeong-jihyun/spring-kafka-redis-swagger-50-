# Spring Kafka + Redis + Swagger — 50 Examples

Java 21 / Spring Boot 기반 학습 프로젝트입니다. Kafka, Redis, Swagger(OpenAPI) 예제 카탈로그와 기본 동작 API를 제공합니다.

> 참고: 아래 50개 항목은 학습 시나리오 카탈로그입니다. 현재 동작 구현은 Kafka 메시지 발행, Redis 문자열 저장/조회/삭제/카운터, 예제 카탈로그 조회입니다. 나머지 항목은 아직 개별 구현이 완료되지 않았습니다.

## 기술 스택
- Java 21, Spring Boot 3.5.6
- Spring Kafka
- Spring Data Redis
- Spring Web, Jakarta Validation
- springdoc-openapi / Swagger UI
- Docker Compose (Kafka, Redis)

## 실행
필수: JDK 21, Maven 3.9+, Docker Compose

1. `docker compose up -d`
2. `mvn spring-boot:run`

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health

## API 테스트

### 시나리오 목록
```bash
curl http://localhost:8080/api/examples
curl http://localhost:8080/api/examples/1
```

### Kafka 발행
```bash
curl -X POST http://localhost:8080/api/kafka/messages \
  -H "Content-Type: application/json" \
  -d '{"key":"order-1001","message":"order-created"}'
```

### Redis 쓰기/읽기/카운터/삭제
```bash
curl -X PUT http://localhost:8080/api/redis/greeting \
  -H "Content-Type: application/json" \
  -d '{"value":"hello redis","ttlSeconds":60}'
curl http://localhost:8080/api/redis/greeting
curl -X POST http://localhost:8080/api/redis/page-views/increment
curl -X DELETE http://localhost:8080/api/redis/greeting
```

## 50개 시나리오
- Kafka (1–20): 메시지 발행, key, partition, listener, consumer group, manual ACK, retry, DLT, batch, headers, JSON, correlation ID, offset, concurrency, transaction, idempotence, callback, pause/resume, lag, schema evolution
- Redis (21–40): string, TTL, counter, delete, existence, expiry refresh, TTL read, list, hash, set, sorted set, cache-aside, invalidation, lock, rate limit, session, idempotency, Pub/Sub, stream, Lua atomic operation
- Integration (41–46): Kafka-to-Redis, Redis dedupe, outbox, replay, cache warming, fallback
- API / Swagger (47–50): OpenAPI metadata, validation, standard errors, pagination/health documentation

## 환경 변수
- `KAFKA_BOOTSTRAP_SERVERS` (기본값 `localhost:9092`)
- `REDIS_HOST` (기본값 `localhost`)
- `REDIS_PORT` (기본값 `6379`)

## 주의
- Docker Compose는 로컬 개발용 단일 Kafka 브로커 구성입니다.
- 인증/인가, TLS, secret 관리, ACL, 관측성, 통합 테스트는 운영 전 별도 구성이 필요합니다.
- 빌드 및 실제 Kafka/Redis 실행 테스트는 아직 검증되지 않았습니다.
