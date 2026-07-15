# 인기 메뉴 목록 조회

## 개요
최근 7일간 주문 횟수 기준 상위 3개 메뉴를 조회한다.

## Endpoint

```
GET /api/v1/menus/popular
```

## Request

### Headers
없음

### Query Parameters
없음 (기본값: 최근 7일, 상위 3개 고정)

## Response

### 200 OK

```json
{
  "period": {
    "from": "2026-07-08",
    "to": "2026-07-15"
  },
  "popularMenus": [
    {
      "rank": 1,
      "menuId": 1,
      "name": "아메리카노",
      "orderCount": 152
    },
    {
      "rank": 2,
      "menuId": 3,
      "name": "카페모카",
      "orderCount": 98
    },
    {
      "rank": 3,
      "menuId": 2,
      "name": "카페라떼",
      "orderCount": 87
    }
  ]
}
```

### Response Fields

| 필드 | 타입 | 설명 |
|---|---|---|
| period.from | String (yyyy-MM-dd) | 집계 시작일 |
| period.to | String (yyyy-MM-dd) | 집계 종료일 |
| rank | Integer | 순위 |
| menuId | Long | 메뉴 ID |
| name | String | 메뉴 이름 |
| orderCount | Integer | 최근 7일간 주문 횟수 |

## Error Cases

| HTTP Status | 에러 코드 | 설명 |
|---|---|---|
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

## 설계 메모

- **정확성 요구사항**: "메뉴별 주문 횟수가 정확해야 합니다" → 매 요청마다 주문 테이블을 직접 집계(GROUP BY)하는 방식이 가장 정확함
- **성능 고려**: 주문량이 많아질 경우 매번 집계 쿼리가 부담될 수 있음
  → 실시간 정확성이 최우선이면 직접 집계, 약간의 지연이 허용되면 배치/캐싱 방식도 고려 가능 (다만 요구사항이 "정확성"을 명시하므로 직접 집계 우선 추천)
- 동시에 여러 메뉴가 같은 주문 횟수일 경우 동점 처리 기준(예: 최신 주문 우선, menuId 오름차순 등)을 정해두면 좋음