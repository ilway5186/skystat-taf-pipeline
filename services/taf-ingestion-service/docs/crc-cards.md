# TAF Ingestion Service CRC 카드 초안

## 전제
- `core`에는 이미 `Taf`, `TafId`, `ForecastPeriod`, `TafParser` 같은 TAF 해석용 핵심 모델이 있다.
- 따라서 `taf-ingestion-service`는 TAF 자체를 다시 모델링하지 않고, `core` 객체를 협력자로 사용한다.
- 이 문서는 `taf-ingestion-service`가 새로 가져야 할 클래스 후보를 `Class / Responsibility / Collaborator` 기준으로만 정리한다.

## core에서 재사용하는 핵심 협력자
- `Taf`
- `TafId`
- `TafParser`
- `ForecastPeriod`

## CRC 카드

### 1. `TafIngestionJob`
- `Responsibility`
  - 5분 주기 전수 폴링 작업을 시작한다
  - 수집 대상 공항 목록을 가져온다
  - 공항별 수집 작업을 bounded concurrency로 위임한다
- `Collaborator`
  - `AirportSource`
  - `AirportTafIngestionService`

### 2. `AirportSource`
- `Responsibility`
  - 수집 대상 공항 코드 목록을 제공한다
  - 현재 운영 대상 공항 집합을 서비스에 노출한다
- `Collaborator`
  - `TafIngestionJob`

### 3. `AirportTafIngestionService`
- `Responsibility`
  - 단일 공항에 대한 TAF 수집 흐름을 조정한다
  - 외부 응답을 가져오고 `core` 파서로 `Taf`를 만든다
  - 중복 여부를 확인하고 저장과 outbox 기록을 한 트랜잭션으로 처리한다
- `Collaborator`
  - `ExternalTafClient`
  - `TafParser`
  - `TafStorageService`
  - `Taf`
  - `TafId`

### 4. `ExternalTafClient`
- `Responsibility`
  - 외부 API에서 공항별 TAF 원문을 조회한다
  - timeout, 재시도, 실패 격리 같은 호출 규칙을 수행한다
  - 응답 원문을 서비스 계층에 전달한다
- `Collaborator`
  - `AirportTafIngestionService`

### 5. `TafStorageService`
- `Responsibility`
  - `TafId` 기준 중복 저장 여부를 판단한다
  - 신규 TAF만 저장한다
  - 저장과 outbox 생성을 같은 트랜잭션에서 처리한다
- `Collaborator`
  - `StoredTafRepository`
  - `OutboxRecordRepository`
  - `OutboxRecordFactory`
  - `Taf`
  - `TafId`

### 6. `StoredTaf`
- `Responsibility`
  - ingestion service가 영속화하는 TAF 저장 레코드를 표현한다
  - `core`의 `Taf`에서 저장에 필요한 값을 보존한다
  - 보관 및 중복 처리 기준이 되는 식별값과 시각 정보를 가진다
- `Collaborator`
  - `Taf`
  - `TafId`
  - `TafStorageService`
  - `StoredTafRepository`

### 7. `OutboxRecord`
- `Responsibility`
  - 발행 대기 이벤트를 표현한다
  - 발행 상태, 재시도 횟수, 마지막 오류를 관리한다
  - 발행 성공/실패에 따라 상태를 전이한다
- `Collaborator`
  - `OutboxRecordFactory`
  - `OutboxPublisherService`
  - `OutboxRecordRepository`

### 8. `OutboxRecordFactory`
- `Responsibility`
  - 신규 `StoredTaf`에 대응하는 outbox 레코드를 생성한다
  - 발행 payload와 aggregate id를 일관된 규칙으로 만든다
- `Collaborator`
  - `StoredTaf`
  - `OutboxRecord`

### 9. `OutboxPublisherService`
- `Responsibility`
  - 발행 대기 outbox를 조회한다
  - 메시지 브로커로 이벤트를 발행한다
  - 결과에 따라 `OutboxRecord` 상태를 갱신한다
- `Collaborator`
  - `OutboxRecordRepository`
  - `MessagePublisher`
  - `OutboxRecord`

### 10. `MessagePublisher`
- `Responsibility`
  - 메시지 브로커에 실제 발행을 수행한다
  - 브로커 전송 성공/실패를 상위 서비스에 반환한다
- `Collaborator`
  - `OutboxPublisherService`

### 11. `TafRetentionService`
- `Responsibility`
  - 보관 기간이 지난 TAF를 배치 단위로 정리한다
  - 삭제 또는 아카이빙 대상을 선별한다
- `Collaborator`
  - `StoredTafRepository`
  - `StoredTaf`

### 12. `StoredTafRepository`
- `Responsibility`
  - `StoredTaf`를 저장하고 조회한다
  - `TafId` 기준 중복 여부를 확인한다
  - 보관 기간 초과 데이터를 배치 조회한다
- `Collaborator`
  - `StoredTaf`
  - `TafStorageService`
  - `TafRetentionService`

### 13. `OutboxRecordRepository`
- `Responsibility`
  - outbox 레코드를 저장하고 조회한다
  - 발행 대기, 실패 상태 레코드를 다시 가져온다
  - 발행 결과를 반영한다
- `Collaborator`
  - `OutboxRecord`
  - `TafStorageService`
  - `OutboxPublisherService`

## 정리
- `core`의 `Taf`, `TafId`, `TafParser`는 ingestion service가 직접 다시 만들 대상이 아니다.
- ingestion service의 핵심 클래스는 `수집`, `중복 검사`, `저장`, `outbox 생성`, `발행`, `보관`을 맡는 서비스 고유 객체들이다.
- 다음 단계에서는 위 카드 중에서 먼저 `StoredTaf`, `OutboxRecord`, `TafStorageService` 경계를 확정하는 것이 자연스럽다.
