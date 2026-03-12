# SkyStat TAF Pipeline

> **실시간 항공 기상 예보(TAF) 수집 및 위험 기상 이벤트 알림 파이프라인**
>
> Event-Driven Architecture (EDA)를 기반으로 하여 대규모 기상 데이터 트래픽을 안정적으로 처리하고, 각 서비스 간의 결합도를 낮춘 멀티 모듈 프로젝트입니다.

## 1. 프로젝트 배경 및 목적
항공 기상(TAF, METAR) 데이터는 평소에는 일정한 트래픽을 유지하지만, 태풍이나 폭설 등 **기상 악화 상황이 발생하면 수많은 공항에서 수정 예보(AMD) 및 특보가 동시다발적으로 폭증**하는 특성을 가집니다.

이러한 도메인 특성을 반영하여, 데이터 수집과 무거운 텍스트 파싱/분석 로직을 분리하고 **Apache Kafka**를 도입했습니다. 이를 통해 트래픽 폭증 시 시스템 과부하를 방지(버퍼링)하고, 수집 서비스와 알림 서비스 간의 결합도를 완전히 끊어내어(Decoupling) 한쪽의 장애가 전체 시스템으로 전파되지 않는 안정적인 데이터 파이프라인을 구축했습니다.

## 2. 아키텍처 및 시스템 구조
본 프로젝트는 **Spring Boot 멀티 모듈**로 진행되며, 핵심 비즈니스 로직(도메인)과 인프라(Kafka, 외부 API)를 격리했습니다.

### 2.1 모듈 구성 (Multi-Module)
- **`domain` (`com.skystat.taf.domain`)**
    - 시스템의 핵심 비즈니스 로직을 담당합니다.
    - 외부 프레임워크(Spring, Kafka) 의존성 없이 순수한 언어 레벨로 작성되었습니다.
    - 복잡한 TAF 텍스트(BECMG, TEMPO 등) 파싱 룰과 위험 기상(Windshear, 저시정 등) 판단 조건을 관리합니다.
- **`services/taf-producer-service` (`com.skystat.taf.producer`)**
    - **역할:** 외부 기상청 API를 스케줄링하여 최신 TAF 데이터를 수집합니다.
    - **특징:** 수집된 데이터를 가공 없이 Kafka Topic(`taf-data-topic`)으로 발행(Publish)하며, 이후의 데이터 소비 과정에 관여하지 않습니다.
- **`services/taf-alarm-service` (`com.skystat.taf.consumer`)**
    - **역할:** Kafka Topic을 구독(Subscribe)하여 실시간 스트리밍되는 TAF 데이터를 수신합니다.
    - **특징:** `domain` 모듈의 파서를 호출하여 데이터를 분석하고, 위험 기상 조건이 충족되면 외부(Slack, Email 등)로 즉시 알림을 발송합니다.

## 3. 핵심 기술 스택
- **Backend:** Java, Spring Boot 4.0.1
- **Architecture:** Event-Driven Architecture (EDA)
- **Message Broker:** Apache Kafka
- **Build Tool:** Gradle

## 4. 주요 기술적 고민 및 해결책
1. **왜 Kafka를 도입했는가?**
    - **장애 격리:** 동기 API 통신과 달리, 알림 서비스(Consumer)가 다운되더라도 수집 서비스(Producer)는 영향을 받지 않고 Kafka에 데이터를 안전하게 적재합니다.
    - **트래픽 완충 (Buffer):** 악천후 시 폭발적으로 증가하는 데이터 수집 요청을 Kafka가 받아내어 분석 서버의 OOM(Out of Memory)을 방지합니다.
    - **이벤트 재생 (Replay):** TAF 파싱 로직에 오류가 발생해 알림이 누락되었을 경우, Consumer의 오프셋(Offset)을 되돌려 과거 데이터를 다시 읽어오고 로직을 완벽히 복구할 수 있습니다.
2. **도메인 로직의 보호**
    - TAF 파싱과 분석이라는 핵심 규칙을 `domain` 모듈로 격리하여, 향후 통계 데이터베이스 저장 서비스나 어드민 UI 서비스가 추가되더라도 코드 중복 없이 100% 재사용할 수 있도록 설계했습니다.

## 5. 디렉토리 구조
```text
/skystat-taf-pipeline
├── /domain                    # TAF 핵심 파싱 로직 및 엔티티
├── /services
│   ├── /taf-producer-service      # TAF 수집 및 Kafka 발행 어댑터
│   └── /taf-alarm-service      # Kafka 구독 및 위험 기상 분석, 알림 발송 어댑터
├── build.gradle               # 루트 빌드 설정 (공통 의존성 관리)
└── settings.gradle            # 모듈 포함 설정
```