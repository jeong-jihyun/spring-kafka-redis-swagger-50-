# Spring Kafka + Redis + Swagger — 50 Examples

Java 21 / Spring Boot 기반 학습 프로젝트입니다. Kafka, Redis, Swagger(OpenAPI)의 핵심 개념과 구현 패턴을 50개 시나리오로 학습합니다.

> **구현 상태 안내:** 현재 실행 가능한 기본 API는 Kafka 메시지 발행, Redis 문자열 저장/조회/삭제/카운터, 예제 카탈로그 조회입니다. 50개 항목 전체가 개별 기능으로 구현된 것은 아닙니다. 각 장의 Markdown 문서에서 현재 구현 코드와 향후 구현 로직을 구분해 설명합니다.

## 문서 목차

| Chapter | 주제 | 범위 |
|---|---|---:|
| 00 | [개발 환경 구성](docs/chapters/00-개발환경-구성.md) | 공통 |
| 01 | [Spring Kafka](docs/chapters/01-kafka.md) | 01–20 |
| 02 | [Spring Data Redis](docs/chapters/02-redis.md) | 21–40 |
| 03 | [Kafka + Redis 통합 패턴](docs/chapters/03-kafka-redis-통합.md) | 41–46 |
| 04 | [REST API + Swagger / OpenAPI](docs/chapters/04-rest-api-swagger.md) | 47–50 |

각 장에는 학습 목표, 처리 흐름, 구현 로직, API 사용 예시(해당 시), 예외 및 운영 시 주의사항을 정리했습니다. Java 주요 클래스에도 코드의 책임과 처리 흐름을 설명하는 주석을 추가했습니다.

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

## 50개 학습 시나리오

- Kafka (01–20): 메시지 발행, key, partition, listener, consumer group, manual ACK, retry, DLT, batch, headers, JSON, correlation ID, offset, concurrency, transaction, idempotence, callback, pause/resume, lag, schema evolution
- Redis (21–40): string, TTL, counter, delete, existence, expiry refresh, TTL read, list, hash, set, sorted set, cache-aside, invalidation, lock, rate limit, session, idempotency, Pub/Sub, stream, Lua atomic operation
- Integration (41–46): Kafka-to-Redis, Redis dedupe, outbox, replay, cache warming, fallback
- API / Swagger (47–50): OpenAPI metadata, validation, standard errors, health endpoint

## 환경 변수
- `KAFKA_BOOTSTRAP_SERVERS` (기본값 `localhost:9092`)
- `REDIS_HOST` (기본값 `localhost`)
- `REDIS_PORT` (기본값 `6379`)

## 주의
- Docker Compose는 로컬 개발용 단일 Kafka 브로커 구성입니다.
- 인증/인가, TLS, secret 관리, ACL, 관측성, 통합 테스트는 운영 전 별도 구성이 필요합니다.
- 빌드 및 실제 Kafka/Redis 실행 테스트는 아직 검증되지 않았습니다.
