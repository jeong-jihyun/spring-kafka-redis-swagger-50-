# Chapter 03. Kafka + Redis 통합 패턴 (예제 41–46)

## 1. 통합의 목적

Kafka는 이벤트 전달과 재처리에 유리하고 Redis는 빠른 조회, 임시 상태, 중복 방지 데이터에 유리하다. 둘을 함께 사용할 때는 데이터 정합성, 중복 이벤트, 장애 복구를 먼저 설계해야 한다.

## 2. 기본 흐름

```text
Producer -> Kafka Topic -> Consumer -> Redis Read Model
                               |
                         retry / DLT
```

consumer가 이벤트를 읽고 Redis에 반영하는 구조에서는 같은 이벤트가 재전달되어도 최종 상태가 깨지지 않도록 멱등 처리를 고려한다.

## 3. 예제 41–46 설계 및 구현 로직

| 번호 | 주제 | 구현 로직 |
|---:|---|---|
| 41 | Kafka to Redis | `@KafkaListener`가 이벤트를 수신하고 eventId를 확인한 뒤 Redis hash/string read model을 갱신한다. 반영 성공 후 offset을 커밋한다. |
| 42 | Redis then Kafka | Redis에 idempotency key를 `SET NX`로 기록해 중복 요청을 제한한 뒤 Kafka에 발행한다. 발행 실패 시 키 보상/재시도 정책을 둔다. 더 강한 보장이 필요하면 Outbox 패턴을 고려한다. |
| 43 | Outbox pattern | 업무 DB 트랜잭션 안에서 도메인 데이터와 outbox row를 함께 저장한다. 별도 publisher가 미발행 row를 Kafka에 전송하고 성공 여부를 기록한다. |
| 44 | Event replay | 보존된 Kafka record를 새 consumer group으로 다시 읽어 Redis read model을 재구축한다. 재구축 전용 key prefix나 임시 인덱스를 사용해 기존 조회에 영향을 줄이지 않는다. |
| 45 | Cache warming | 애플리케이션 시작 또는 배포 후 인기 데이터 목록을 조회해 Redis에 적재한다. 요청 트래픽과 경합하지 않도록 batch 크기와 속도를 제한한다. |
| 46 | Fallback behavior | Redis가 일시적으로 실패하면 원본 DB fallback 여부를 결정한다. 무제한 fallback은 DB 과부하를 만들 수 있으므로 timeout, circuit breaker, 제한된 재시도를 둔다. |

## 4. 신뢰성 체크리스트

1. **중복 이벤트:** eventId 기반 멱등성 키 또는 version 비교를 사용한다.
2. **순서:** 같은 aggregate key를 Kafka record key로 사용한다.
3. **장애 격리:** Redis 장애가 consumer 전체 정지로 이어질지, retry topic으로 격리할지 정한다.
4. **재처리:** DLT 메시지의 원인과 재처리 가능 여부를 운영자가 확인할 수 있어야 한다.
5. **정합성:** Kafka offset commit과 Redis write는 하나의 분산 트랜잭션이 아니다. 중간 실패 시 재전달을 허용하는 설계가 일반적이다.

## 5. 현재 구현 상태
현재 저장소에는 Kafka producer와 Redis 기본 명령 서비스가 있지만 Kafka consumer와 Redis read model을 연결하는 통합 흐름은 아직 구현되지 않았다. 이 장의 패턴은 구현 가이드이며 실행 코드로 검증된 기능이 아니다.
