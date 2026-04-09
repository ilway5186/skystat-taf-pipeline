# core

`core` 모듈은 TAF를 이해하고 해석하기 위한 공통 핵심 모듈이다.

이 모듈은 각 서비스의 비즈니스 도메인을 대신하지 않는다.  
대신, 모든 서비스가 동일한 방식으로 TAF를 해석하고 다룰 수 있도록 공통 실행 엔진 역할을 한다.

## 역할

- TAF 핵심 도메인 모델 제공
  - `Taf` Entity
  - `ForecastBody`, `ForecastPeriod`, `TafId` 등 VO
- TAF 해석 규칙 제공
  - `FM`, `BECMG`, `TEMPO`, `PROB*` 등 change indicator 의미
  - reference policy
  - TAF 불변식, specification
- TAF parser 제공
  - header parser
  - forecast field parser
  - body parser
  - 최종 `TafParser`
- 공통 예외 및 검증 규칙 제공

## 포함하지 않는 것

`core`는 다음 책임을 가지지 않는다.

- 외부 API 호출
- DB 저장
- 메시지 큐 발행
- 알림 정책 결정
- 수집 상태 관리
- 서비스별 workflow

이 책임들은 각 서비스가 가져야 한다.

## 서비스와의 관계

각 서비스는 자신의 비즈니스 영역을 기준으로 별도 도메인을 설계해야 한다.

예:

- ingestion service
  - 외부 API 조회
  - 적재 정책
  - 중복 처리
  - 저장
- alarm service
  - 위험 조건 판단
  - 알림 정책
  - 발행 대상 결정
  - suppress / retry

이 과정에서 TAF 해석이 필요할 때 `core`를 주입받아 사용한다.

즉 구조는 다음과 같다.

- `core`
  - TAF를 이해하는 방법
- 각 서비스
  - 해석된 TAF를 자신의 비즈니스에 어떻게 사용할지

## 설계 원칙

- `core`는 TAF 해석의 단일 진실 공급원이다.
- 서비스는 TAF 해석 규칙을 자체적으로 다시 구현하지 않는다.
- 공통 규칙은 `core`에 모은다.
- 서비스 고유 정책은 각 서비스 모듈에 둔다.
- `core`는 가능한 한 순수 Java와 도메인 규칙 중심으로 유지한다.

## 현재 구조

- `domain.entity`
  - 핵심 Entity
- `domain.vo`
  - TAF 및 weather 관련 VO
- `domain.condition`
  - TAF 분석용 조건식
- `domain.spec`
  - Entity 불변식 specification
- `domain.service.parser`
  - TAF parser
- `exception`
  - 공통 예외 및 에러 코드

## 비유

`core`는 각 서비스의 도메인 그 자체라기보다,  
TAF를 실행하고 해석하는 공통 엔진에 가깝다.

Java 애플리케이션이 JVM 위에서 실행되듯,  
TAF 관련 서비스들은 `core` 위에서 동일한 TAF 의미 체계를 공유한다.
