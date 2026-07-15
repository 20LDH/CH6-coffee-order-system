# 커피 메뉴 목록 조회

## 개요
등록된 커피 메뉴 전체 목록을 조회한다.

## Endpoint

```
GET /api/v1/menus
```

## Request

### Headers
없음

### Query Parameters
없음

## Response

### 200 OK

```json
{
  "menus": [
    {
      "menuId": 1,
      "name": "아메리카노",
      "price": 4000
    },
    {
      "menuId": 2,
      "name": "카페라떼",
      "price": 4500
    }
  ]
}
```

### Response Fields

| 필드 | 타입 | 설명 |
|---|---|---|
| menuId | Long | 메뉴 고유 ID |
| name | String | 메뉴 이름 |
| price | Integer | 가격 (원) |

## Error Cases

| HTTP Status | 에러 코드 | 설명 |
|---|---|---|
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

## 설계 메모

- 메뉴 목록은 자주 조회되고 변경 빈도가 낮으므로 캐싱(Redis 등) 적용을 고려할 수 있음
- 판매 중단 메뉴 구분이 필요하다면 `isAvailable` 필드 추가 가능 (필수 요구사항은 아님)