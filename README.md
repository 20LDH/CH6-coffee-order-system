# CH6-coffee-order-system

커피숍 주문 시스템 (K사 서버 개발 사전과제)

- **핵심 기능**: 메뉴 조회, 포인트 충전, 주문/결제, 인기 메뉴 조회
- **핵심 설계 관점**: 동시성 제어, 데이터 일관성, 다수 서버 인스턴스 대응
- **스택**: Java 21, Spring Boot, Gradle, JPA

## API 명세

### 1. 커피 메뉴 목록 조회

`GET /api/v1/menus`

| 필드 | 타입 | 설명 |
|---|---|---|
| menuId | Long | 메뉴 고유 ID |
| name | String | 메뉴 이름 |
| price | Integer | 가격 (원) |

| HTTP Status | 에러 코드 | 설명 |
|---|---|---|
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

> 상세: [docs/api/menu-list-api.md](docs/api/menu-list-api.md)

### 2. 포인트 충전하기

`POST /api/v1/points/charge` — 사용자 식별값과 충전 금액을 입력받아 포인트를 충전한다. (1원 = 1P)

**Request**

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| userId | String | Y | 사용자 식별값 |
| amount | Integer | Y | 충전 금액 (원, 양수만 허용) |

**Response**

| 필드 | 타입 | 설명 |
|---|---|---|
| userId | String | 사용자 식별값 |
| chargedAmount | Integer | 이번에 충전된 금액 |
| totalPoint | Integer | 충전 후 총 보유 포인트 |

| HTTP Status | 에러 코드 | 설명 |
|---|---|---|
| 400 | INVALID_AMOUNT | 충전 금액이 0 이하이거나 유효하지 않음 |
| 400 | INVALID_USER_ID | 사용자 식별값 누락 또는 형식 오류 |
| 404 | USER_NOT_FOUND | 존재하지 않는 사용자 (미사용) |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

> 상세: [docs/api/point-charge-api.md](docs/api/point-charge-api.md)

### 3. 커피 주문/결제하기

`POST /api/v1/orders` — 사용자 식별값과 메뉴 ID를 입력받아 주문을 생성하고, 보유 포인트에서 주문 금액을 차감하여 결제를 진행한다.

**Request**

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| userId | String | Y | 사용자 식별값 |
| menuId | Long | Y | 주문할 메뉴 ID |

**Response**

| 필드 | 타입 | 설명 |
|---|---|---|
| orderId | Long | 생성된 주문 ID |
| userId | String | 사용자 식별값 |
| menuId | Long | 주문한 메뉴 ID |
| menuName | String | 주문한 메뉴 이름 |
| paidAmount | Integer | 결제(차감)된 금액 |
| remainingPoint | Integer | 결제 후 남은 포인트 |
| orderedAt | String (ISO 8601) | 주문 시각 |

| HTTP Status | 에러 코드 | 설명 |
|---|---|---|
| 400 | INVALID_MENU_ID | 존재하지 않는 메뉴 ID |
| 400 | INVALID_USER_ID | 사용자 식별값 누락 또는 형식 오류 |
| 409 | INSUFFICIENT_POINT | 보유 포인트가 주문 금액보다 적음 |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |
| 502 | DATA_PLATFORM_SEND_FAILED | 데이터 수집 플랫폼 전송 실패 |

> 상세: [docs/api/order-payment-api.md](docs/api/order-payment-api.md)

### 4. 인기 메뉴 목록 조회

`GET /api/v1/menus/popular` — 최근 7일간 주문 횟수 기준 상위 3개 메뉴를 조회한다.

**Response**

| 필드 | 타입 | 설명 |
|---|---|---|
| period.from / period.to | String (yyyy-MM-dd) | 집계 기간 |
| rank | Integer | 순위 |
| menuId | Long | 메뉴 ID |
| name | String | 메뉴 이름 |
| orderCount | Integer | 최근 7일간 주문 횟수 |

| HTTP Status | 에러 코드 | 설명 |
|---|---|---|
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

> 상세: [docs/api/popular-menu-api.md](docs/api/popular-menu-api.md)

## ERD (테이블 명세)

별도 회원가입/로그인 API가 없어 `users` 테이블 없이 `userId`를 클라이언트가 보낸 문자열 식별값 그대로 사용한다. `user_point`는 최초 충전 시점에 자동 생성된다.

```mermaid
erDiagram
    menu ||--o{ orders : "주문됨"
    orders ||--o| point_history : "포인트 사용 이력 연결"

    menu {
        BIGINT menu_id PK "메뉴 ID"
        VARCHAR name "메뉴 이름"
        INT price "가격(원)"
    }

    user_point {
        BIGINT id PK "포인트 계정 ID"
        VARCHAR user_id UK "사용자 식별값"
        INT balance "보유 포인트"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
    }

    point_history {
        BIGINT id PK "이력 ID"
        VARCHAR user_id "사용자 식별값"
        VARCHAR type "변동 타입"
        INT amount "변동 금액"
        INT balance_after "변동 후 잔액"
        BIGINT order_id FK "관련 주문 ID"
        DATETIME created_at "생성일시"
    }

    orders {
        BIGINT order_id PK "주문 ID"
        VARCHAR user_id "사용자 식별값"
        BIGINT menu_id FK "메뉴 ID"
        VARCHAR menu_name "주문 당시 메뉴명"
        INT paid_amount "결제(차감) 금액"
        DATETIME ordered_at "주문일시"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
    }
