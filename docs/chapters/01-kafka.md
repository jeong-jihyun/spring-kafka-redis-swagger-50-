# Chapter 01. Spring Kafka (예제 01–20)

## 1. Kafka 처리 구조

Kafka는 producer가 topic에 record를 발행하고 consumer가 구독해 처리하는 분산 로그 기반 메시징 시스템이다.

```text
REST Client
   |
   v
ExampleController
   |
   v
KafkaDemoService
   |
   v
KafkaTemplate ----> topic: demo.events ----> Consumer Group
                         |
                    partition 0..2
```

현재 코드에서 실제로 연결된 경로는 REST API → `KafkaDemoService` → `KafkaTemplate` → `demo.events` 토픽까지다. consumer listener, 재시도, DLT 등은 아래에서 설계 로직을 설명하는 학습 항목이며 아직 별도 실행 코드가 추가된 상태는 아니다.

## 2. 현재 구현: 메시지 발행 (예제 01)

관련 파일:
- `config/KafkaTopics.java`
- `service/KafkaDemoService.java`
- `controller/ExampleController.java`

### 구현 순서
1. 애플리케이션 시작 시 `KafkaTopics`가 `demo.events` 토픽을 3개 파티션, 복제 계수 1로 선언한다.
2. 클라이언트가 `POST /api/kafka/messages`에 key와 message를 전달한다.
3. Controller의 Jakarta Validation이 빈 값과 길이를 검사한다.
4. Controller는 Kafka 전송을 서비스 계층에 위임한다.
5. `KafkaTemplate.send(topic, key, value)`가 비동기 전송을 요청한다.

요청 예시:

```bash
curl -X POST http://localhost:8080/api/kafka/messages \
  -H "Content-Type: application/json" \
  -d '{"key":"order-1001","message":"order-created"}'
```

현재 API의 `202 Accepted`는 전송 요청을 접수했다는 의미다. broker 저장 완료를 보장하는 응답으로 사용하려면 `send()`가 반환하는 Future를 확인하고 성공/실패 결과를 처리해야 한다.

### 핵심 코드 설명

```java
kafkaTemplate.send("demo.events", key, message);
```

첫 번째 인자는 topic, 두 번째 인자는 record key, 세 번째 인자는 payload다. key는 동일 key가 같은 partition으로 매핑되도록 하여 key 단위 순서를 유지하는 데 도움을 준다. 전체 topic의 모든 메시지에 대해 전역 순서를 보장하는 것은 아니다.

## 3. 예제 02–20 설계 및 구현 로직

| 번호 | 주제 | 구현 로직 |
|---:|---|---|
| 02 | Message key | 도메인 식별자(예: orderId)를 key로 전달해 동일 주문 이벤트가 같은 partition에 배치되게 한다. |
| 03 | Partitioned topic | `NewTopic`에 partition 수를 지정한다. partition 수는 병렬 처리량과 key 분산에 영향을 준다. |
| 04 | Consumer listener | `@KafkaListener(topics="demo.events")` 메서드에서 record를 수신하고 업무 서비스에 위임한다. |
| 05 | Consumer group | 동일 group.id의 consumer들이 partition을 분담한다. 같은 group 안에서 하나의 partition은 한 시점에 한 consumer에 할당된다. |
| 06 | Manual acknowledgement | 수동 ack 모드에서 업무 처리가 성공한 뒤에만 acknowledge한다. 실패 시 재처리 정책과 중복 처리 가능성을 함께 설계한다. |
| 07 | Retry topic | 일시적 네트워크 오류 등 재시도 가능한 오류를 지연된 retry topic으로 보낸다. 재시도 횟수와 지연 간격을 제한한다. |
| 08 | Dead-letter topic | 재시도 소진 또는 복구 불가능한 메시지를 DLT로 보내 원본 topic, partition, offset, 예외 정보를 보존한다. |
| 09 | Batch listener | listener batch 모드로 여러 record를 한 번에 받아 DB 작업 등을 묶어 처리한다. 부분 실패 처리 기준이 필요하다. |
| 10 | Headers | traceId, eventType, schemaVersion 같은 메타데이터를 Kafka record header에 넣고 consumer에서 읽는다. |
| 11 | JSON payload | DTO를 JSON serializer로 직렬화한다. 타입 정보에만 의존하지 말고 명시적인 event schema/version을 관리한다. |
| 12 | Correlation ID | 요청 진입 시 correlationId를 만들거나 전달받아 header에 넣고 consumer 로그까지 전파한다. |
| 13 | Partition/offset | 전송 결과 metadata에서 partition과 offset을 확인한다. 운영 추적 및 장애 분석에 활용한다. |
| 14 | Consumer concurrency | listener container concurrency를 조정한다. 실제 병렬도는 할당된 partition 수에 제한된다. |
| 15 | Transactional publish | Kafka transaction 설정과 transaction id prefix를 구성하고 관련 record들을 하나의 Kafka transaction으로 발행한다. |
| 16 | Idempotent producer | producer idempotence를 활성화해 producer 재시도에 따른 중복 append 위험을 줄인다. consumer 업무 중복까지 자동 해결하지는 않는다. |
| 17 | Producer callback | send Future의 완료 콜백에서 metadata를 기록하고 실패를 로깅·알림한다. |
| 18 | Pause/resume | listener container를 pause/resume해 외부 시스템 장애나 backpressure 상황에서 소비 속도를 제어한다. |
| 19 | Consumer lag | consumer committed offset과 log end offset의 차이를 측정해 지연을 모니터링한다. |
| 20 | Schema evolution | schema registry 또는 버전 필드를 사용하고 backward/forward compatibility 규칙에 따라 이벤트 필드를 변경한다. |

## 4. 예외와 신뢰성 설계

- 재시도는 모든 예외에 무조건 적용하지 않는다. 일시적 오류와 영구적 오류를 구분한다.
- consumer는 at-least-once 처리에서 중복 전달될 수 있으므로 업무 로직을 멱등하게 설계한다.
- 메시지 처리 성공 후 offset을 커밋하는 순서를 지킨다.
- DLT는 모니터링과 재처리 절차가 있어야 하며 단순히 메시지를 버리는 장소로 사용하지 않는다.
- 메시지에는 비밀번호, 토큰 등 민감정보를 넣지 않는다.

## 5. 확인 포인트
현재 구현 완료 범위는 producer 발행 API와 topic 선언이다. 위 표의 consumer·retry·transaction 기능은 설계 설명이며 코드 구현 및 통합 테스트가 추가로 필요하다.
