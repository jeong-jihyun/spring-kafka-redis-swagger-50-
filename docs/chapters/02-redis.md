# Chapter 02. Spring Data Redis (예제 21–40)

## 1. Redis 접근 구조

```text
REST Client
   |
ExampleController
   |
RedisDemoService
   |
StringRedisTemplate
   |
Redis Server
```

Controller는 HTTP 요청과 응답을 담당하고, Redis 명령은 Service 계층에 모은다. 이 분리는 저장소 기술을 변경하거나 테스트 대역(mock)을 적용하기 쉽게 한다.

## 2. 현재 구현: String, TTL, Counter (예제 21–27 중 일부)

관련 파일:
- `service/RedisDemoService.java`
- `controller/ExampleController.java`

### 저장

```bash
curl -X PUT http://localhost:8080/api/redis/greeting \
  -H "Content-Type: application/json" \
  -d '{"value":"hello redis","ttlSeconds":60}'
```

서비스는 TTL이 양수이면 `set(key, value, Duration)`을 사용하고, 0이면 만료 시간 없이 저장한다. TTL은 초 단위로 입력받는다.

### 조회

```bash
curl http://localhost:8080/api/redis/greeting
```

key가 존재하면 value와 남은 TTL을 반환한다. 존재하지 않으면 Controller가 HTTP 404를 반환한다.

### 카운터 증가

```bash
curl -X POST http://localhost:8080/api/redis/page-views/increment
```

Redis의 `INCR` 연산은 서버에서 원자적으로 실행된다. 여러 요청이 동시에 들어와도 단순한 애플리케이션 read-modify-write보다 안전하다.

### 삭제

```bash
curl -X DELETE http://localhost:8080/api/redis/greeting
```

삭제 결과(Boolean)를 응답에 담는다.

## 3. 예제 21–40 설계 및 구현 로직

| 번호 | 주제 | 구현 로직 |
|---:|---|---|
| 21 | String set/get | `opsForValue().set/get`으로 문자열 key-value를 저장하고 조회한다. |
| 22 | TTL key | 저장 시 Duration을 전달해 만료 시간을 설정한다. TTL이 필요한 캐시는 무기한 저장을 피한다. |
| 23 | Increment counter | `opsForValue().increment`로 Redis INCR를 호출한다. 초기 key가 없으면 0에서 증가한다. |
| 24 | Delete key | `redisTemplate.delete(key)`로 key를 삭제하고 삭제 여부를 반환한다. |
| 25 | Key existence | `hasKey(key)`로 존재 여부를 확인한다. 확인 직후 다른 요청이 삭제할 수 있으므로 check-then-act 로직의 동기화 수단으로 쓰지 않는다. |
| 26 | Refresh expiry | `expire(key, Duration)`으로 기존 key의 만료 시간을 갱신한다. |
| 27 | Read TTL | `getExpire(key)`로 남은 만료 시간을 확인한다. 반환값의 특수 의미(만료 없음/키 없음)를 구분한다. |
| 28 | List push | `opsForList().rightPush`로 대기열 형태의 데이터를 추가한다. 소비 방향과 최대 길이를 함께 설계한다. |
| 29 | Hash fields | `opsForHash().put/entries`로 객체의 필드를 hash에 저장한다. |
| 30 | Set membership | `opsForSet().add/isMember`로 중복 없는 구성원 집합을 관리한다. |
| 31 | Sorted set ranking | `opsForZSet().add/range`로 점수 기반 순위표를 구현한다. 동점 정렬 기준을 정의한다. |
| 32 | Cache-aside | 캐시 hit면 즉시 반환하고 miss면 원본 저장소에서 조회한 뒤 캐시에 적재한다. |
| 33 | Cache invalidation | 원본 데이터 변경 성공 후 관련 cache key를 삭제하거나 갱신한다. 오래된 데이터가 남는 시간과 경합 조건을 고려한다. |
| 34 | Distributed lock | `SET key token NX PX ttl` 방식으로 잠금을 획득한다. 해제 시에는 저장된 token이 본인 token과 같은지 Lua script로 확인 후 삭제한다. |
| 35 | Rate limiting | 고정 창 또는 sliding window 알고리즘을 선택하고 INCR와 만료 설정을 원자적으로 처리한다. |
| 36 | Session storage | 세션 식별자를 key로 두고 사용자 세션 데이터를 TTL과 함께 저장한다. 토큰 원문이나 민감정보 노출에 주의한다. |
| 37 | Idempotency key | 요청 고유 ID를 짧은 TTL로 저장해 이미 처리한 요청을 판별한다. 업무 결과 저장과 중복 방지 기록의 원자성도 고려한다. |
| 38 | Pub/Sub | publisher가 channel에 발행하고 subscriber가 구독한다. Pub/Sub 메시지는 구독자가 오프라인이면 유실될 수 있다. |
| 39 | Stream append | `opsForStream().add`로 이벤트를 적재하고 consumer group으로 분산 소비한다. pending entry와 ack를 관리한다. |
| 40 | Lua atomic operation | 여러 Redis 명령을 Lua script 하나로 실행해 중간 상태 노출을 막는다. script 실행 시간과 key 접근 범위를 제한한다. |

## 4. Key 설계 규칙

- 도메인 prefix를 사용한다: `user:100:profile`, `order:200:status`.
- TTL 단위를 이름과 API에서 명확히 한다.
- 큰 객체를 무분별하게 저장하지 않고 필요한 필드만 저장한다.
- 운영 환경에서는 `KEYS *` 대신 SCAN 계열을 사용한다.
- 캐시에는 원본 데이터와 만료·무효화 정책을 함께 정의한다.

## 5. 확인 포인트
현재 코드에서 실제 API로 연결된 Redis 기능은 String 저장/조회/삭제와 counter 증가다. Redis service에 일부 존재 여부·TTL·list·hash 메서드가 있으나, 나머지 예제는 개별 API, 오류 처리 및 테스트를 추가해야 한다.
