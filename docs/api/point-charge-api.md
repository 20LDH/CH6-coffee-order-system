# 포인트 충전하기

## 개요
사용자 식별값과 충전 금액을 입력받아 포인트를 충전한다. (1원 = 1P)

## Endpoint

```
POST /api/v1/points/charge
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
  "amount": 10000
}
```

### Request Fields

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| userId | String | Y | 사용자 식별값 |
| amount | Integer | Y | 충전 금액 (원, 양수만 허용) |

## Response

### 200 OK

```json
{
  "userId": "user-1234",
  "chargedAmount": 10000,
  "totalPoint": 25000
}
```

### Response Fields

| 필드 | 타입 | 설명 |
|---|---|---|
| userId | String | 사용자 식별값 |
| chargedAmount | Integer | 이번에 충전된 금액 |
| totalPoint | Integer | 충전 후 총 보유 포인트 |

## Error Cases

| HTTP Status | 에러 코드 | 설명 |
|---|---|---|
| 400 | INVALID_AMOUNT | 충전 금액이 0 이하이거나 유효하지 않음 |
| 400 | INVALID_USER_ID | 사용자 식별값 누락 또는 형식 오류 |
| 404 | USER_NOT_FOUND | 존재하지 않는 사용자 (사전 등록 방식일 경우) |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

## 설계 메모

- **동시성 이슈**: 동일 사용자가 짧은 시간에 여러 번 충전 요청을 보낼 경우 race condition 발생 가능
  → 비관적 락(Pessimistic Lock) 또는 낙관적 락(Optimistic Lock) + 재시도, 혹은 DB의 원자적 UPDATE(`UPDATE ... SET point = point + :amount`) 방식 고려
- **데이터 일관성**: 충전 이력을 별도 테이블(`point_history`)로 남겨 잔액과 이력이 항상 대조 가능하도록 설계 고려
- 사용자가 사전 등록 없이 최초 충전 시 자동 생성되는 방식으로 갈지, 별도 회원가입이 필요한지 설계 시 결정 필요