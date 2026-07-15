# AGENTS.md

이 저장소에서 코딩 에이전트가 작업할 때 따르는 규칙이다.

---

## 프로젝트 개요

- **과제**: 커피숍 주문 시스템 (K사 서버 개발 사전과제)
- **핵심 기능**: 메뉴 조회, 포인트 충전, 주문/결제, 인기 메뉴 조회
- **핵심 설계 관점**: 동시성 제어, 데이터 일관성, 다수 서버 인스턴스 대응
- **스택**: Java 21, Spring Boot, Gradle, JPA
- **API 명세**: `docs/api/*.md`

---

## 개발 작업 흐름 (Plan → Generate → Evaluate)

```
1. Plan (계획)
      │
   ══[ 사람 승인 ]══   ← 승인 전에는 코드 작성 금지
      │
2. Generate (생성)
      │
3. Evaluate (평가)
      │
   통과? ──no──> Plan으로 돌아가 재계획 또는 Generate 재시도
      │yes
      ▼
    완료
```

### 1. Plan
- 코드를 만들기 전에 아래를 먼저 설명한다.
    - 어떤 API(`docs/api/*.md` 기준)를 다루는지
    - 어느 계층까지 만들지 (entity / repository / service / controller)
    - 동시성·일관성이 걸리는 로직이면 어떤 전략을 쓸지 (예: 트랜잭션 범위, 락)
- 사용자가 이 계획에 **명시적으로 동의**하기 전에는 Generate로 넘어가지 않는다.

### 2. Generate
- 승인된 계획 범위 안에서만 구현한다. 요청하지 않은 기능·리팩토링을 임의로 추가하지 않는다.
- `docs/api/*.md`에 정의된 요청/응답 필드, 에러 코드, 공통 응답 형식을 그대로 따른다.
- 범위를 벗어나야 할 상황이 생기면 먼저 알리고 Plan을 다시 승인받는다.

### 3. Evaluate
- `./gradlew test`를 실행하고 결과를 사실대로 보고한다 (통과/실패 개수, 실패 원인).
- 다음을 함께 확인한다.
    - `docs/api/*.md`의 스펙과 실제 구현이 일치하는가
    - 포인트 차감·주문 생성처럼 동시성이 걸린 로직의 트랜잭션 경계가 올바른가
- 실패 시 원인을 설명하고, Generate를 다시 하거나 Plan부터 다시 잡는다.

---

## 기본 컨벤션

### 패키지 구조
도메인 단위로 나눈다. 계층별(controller, service 등) 하위 패키지로 다시 쪼개지 않는다.

```
com.example.ch6coffeeordersystem
├── common      (ApiResponse, 예외 처리 등 공통 요소)
├── menu        (Menu, MenuController, MenuService, MenuRepository, MenuResponse)
├── point       (포인트 관련 전부)
└── order       (주문 관련 전부)
```

### 코드 스타일
- 생성자 주입 사용 (필드 주입 `@Autowired` 지양)
- DTO는 `record` 사용
- 조회 전용 서비스 메서드는 `@Transactional(readOnly = true)` 명시
- Entity에는 setter를 만들지 않고, 필요한 변경은 의미 있는 메서드로 노출
- 에러 응답은 공통 형식(`{status, message, data}` 또는 `{status, message, error}`)을 따른다