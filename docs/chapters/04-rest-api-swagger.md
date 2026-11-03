# Chapter 04. REST API + Swagger / OpenAPI (예제 47–50)

## 1. 계층 구조

```text
HTTP Request
    |
Controller  -- 입력 검증 / HTTP status / 응답
    |
Service     -- 업무 처리
    |
Kafka / Redis
```

Controller는 HTTP 계약에 집중하고 Kafka·Redis 명령은 Service로 위임한다. Swagger UI는 코드의 OpenAPI annotation과 설정을 읽어 API 명세 및 테스트 화면을 제공한다.

## 2. 예제 47–50 구현 로직

| 번호 | 주제 | 구현 로직 |
|---:|---|---|
| 47 | OpenAPI metadata | `OpenApiConfig`에서 API 제목, 버전, 설명을 설정한다. 각 Controller 메서드에는 `@Operation`, 그룹에는 `@Tag`를 붙인다. |
| 48 | Request validation | request record 필드에 `@NotBlank`, `@Size`, `@Min` 등을 선언하고 Controller 파라미터에 `@Valid`를 적용한다. 잘못된 요청은 서비스 실행 전에 거부한다. |
| 49 | Standard error response | `@RestControllerAdvice`에서 Validation, NotFound, 외부 시스템 예외를 공통 오류 DTO로 변환한다. 오류 코드, 메시지, timestamp, traceId를 제공한다. |
| 50 | Health endpoint | Actuator health endpoint와 Kafka/Redis health indicator를 사용해 프로세스 상태와 의존 서비스 상태를 구분한다. readiness와 liveness 의미를 분리한다. |

## 3. 현재 제공 API

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/examples` | 50개 시나리오 카탈로그 조회 |
| GET | `/api/examples/{id}` | 개별 시나리오 조회 |
| POST | `/api/kafka/messages` | Kafka topic에 메시지 발행 요청 |
| PUT | `/api/redis/{key}` | Redis 문자열 저장 |
| GET | `/api/redis/{key}` | Redis 문자열 조회 |
| DELETE | `/api/redis/{key}` | Redis key 삭제 |
| POST | `/api/redis/{key}/increment` | Redis counter 증가 |

## 4. Swagger 확인

애플리케이션 실행 후 다음 주소를 연다.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 5. API 설계 원칙

- 생성/비동기 접수는 적절한 HTTP status를 반환한다.
- 입력 오류는 400, 없는 리소스는 404, 서버/의존 서비스 오류는 5xx로 구분한다.
- 예외 메시지에 stack trace, 내부 host, credential을 노출하지 않는다.
- 페이지네이션을 도입할 때는 page, size의 최대값과 정렬 허용 필드를 제한한다.
- health endpoint는 외부에 무분별하게 노출하지 않고 배포 플랫폼의 접근 정책을 따른다.

## 6. 현재 구현 상태
OpenAPI metadata와 기본 Controller annotation, 일부 request validation 및 Actuator health endpoint는 설정되어 있다. 공통 오류 응답 Advice와 pagination 예제는 아직 별도 구현이 필요하다.
