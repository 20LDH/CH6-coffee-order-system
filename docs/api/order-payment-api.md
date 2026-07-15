# 커피 주문/결제하기

## 개요
사용자 식별값과 메뉴 ID를 입력받아 주문을 생성하고, 보유 포인트에서 주문 금액을 차감하여 결제를 진행한다. 결제 완료 후 주문 내역을 데이터 수집 플랫폼으로 실시간 전송한다.

## Endpoint

```
POST /api/v1/orders
```

## Request

### Headers

| 헤더 | 필수 | 설명 |
|---|---|---|
| Content-Type | Y | application/json |

### Body

```json
{
  "userId": "user-1234",
  "menuId": 1
}
```

### Request Fields

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| userId | String | Y | 사용자 식별값 |
| menuId | Long | Y | 주문할 메뉴 ID |

## Response

### 201 Created

```json
{
  "orderId": 5001,
  "userId": "user-1234",
  "menuId": 1,
  "menuName": "아메리카노",
  "paidAmount": 4000,
  "remainingPoint": 6000,
  "orderedAt": "2026-07-15T10:30:00"
}
```

### Response Fields

| 필드 | 타입 | 설명 |
|---|---|---|
| orderId | Long | 생성된 주문 ID |
| userId | String | 사용자 식별값 |
| menuId | Long | 주문한 메뉴 ID |
| menuName | String | 주문한 메뉴 이름 |
| paidAmount | Integer | 결제(차감)된 금액 |
| remainingPoint | Integer | 결제 후 남은 포인트 |
| orderedAt | String (ISO 8601) | 주문 시각 |

## Error Cases

| HTTP Status | 에러 코드 | 설명 |
|---|---|---|
| 400 | INVALID_MENU_ID | 존재하지 않는 메뉴 ID |
| 400 | INVALID_USER_ID | 사용자 식별값 누락 또는 형식 오류 |
| 409 | INSUFFICIENT_POINT | 보유 포인트가 주문 금액보다 적음 |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |
| 502 | DATA_PLATFORM_SEND_FAILED | 데이터 수집 플랫폼 전송 실패 (주문 자체는 성공 처리할지 결정 필요) |

## 설계 메모

이 API가 과제에서 동시성 · 일관성 이슈가 가장 몰려있는 핵심 API임.

- **동시성 이슈**: 동일 사용자가 여러 인스턴스/여러 요청으로 동시에 주문할 경우, 포인트가 이중 차감되거나 음수가 되지 않도록 방지 필요
  → DB 락(비관적 락) 또는 분산 락(Redis + Redisson) 중 택일하고, 왜 그 방식을 선택했는지 근거 정리
- **데이터 일관성**: "포인트 차감"과 "주문 생성"은 하나의 트랜잭션으로 묶여야 함 (둘 중 하나만 성공하는 상황 방지)
- **다수 서버 인스턴스 대응**: 로컬 synchronized 방식은 여러 서버에서 동작 시 무력화됨 → DB 레벨 락 또는 분산 락 필요
- **데이터 수집 플랫폼 전송**: 주문/결제 성공 이후 별도 이벤트로 처리(Mock API 호출 또는 메시지 큐 발행)하여, 전송 실패가 주문 자체의 실패로 이어지지 않도록 설계하는 것을 권장 (예: 비동기 처리 + 재시도 또는 Outbox 패턴)