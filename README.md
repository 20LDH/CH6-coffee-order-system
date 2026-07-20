# CH6-coffee-order-system

커피숍 주문 시스템 (K사 서버 개발 사전과제)

- **핵심 기능**: 메뉴 조회, 포인트 충전, 주문/결제, 인기 메뉴 조회
- **핵심 설계 관점**: 동시성 제어, 데이터 일관성, 다수 서버 인스턴스 대응
- **스택**: Java 21, Spring Boot, Gradle, JPA

## 설계 문서

| 구분 | 문서 |
|---|---|
| API 명세 | [메뉴 목록 조회](docs/api/menu-list-api.md) · [포인트 충전](docs/api/point-charge-api.md) · [주문/결제](docs/api/order-payment-api.md) · [인기 메뉴 조회](docs/api/popular-menu-api.md) |
| ERD (테이블 명세) | [docs/db/erd.md](docs/db/erd.md) |
| 코드 컨벤션 | [docs/convention.md](docs/convention.md) |
| 작업 가이드 | [docs/workflow/plan-guide.md](docs/workflow/plan-guide.md) · [docs/workflow/logs-guide.md](docs/workflow/logs-guide.md) |

## 실행 방법

```bash
./gradlew bootRun
```

## 테스트

```bash
./gradlew test
```
