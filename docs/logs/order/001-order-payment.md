# 001-order-payment — 주문/결제 (로그)

## Attempt 1 — 2026-07-20  ✅ PASS
- 시도: `POST /api/v1/orders` 구현
  - Entity: `Order`(주문 1건 = 메뉴 1개, 메뉴명/가격 스냅샷 저장)
  - Repository: `OrderRepository`, `UserPointRepository.findByUserIdForUpdate`(비관적 락, `@Lock(PESSIMISTIC_WRITE)` + `@Query`)
  - Entity 추가: `UserPoint.deduct(amount)`(잔액 부족 시 `IllegalStateException`), `PointHistory.ofUse(...)` 팩토리
  - DTO: `OrderCreateRequest`(`@NotBlank userId`, `@NotNull menuId`), `OrderCreateResponse`
  - Service: `OrderService.order()` — 메뉴 조회 → 포인트 계정 비관적 락 조회 → 잔액 검증/차감 → 주문 저장 → 포인트 사용(USE) 이력 저장을 하나의 `@Transactional`로 묶음
  - Controller: `OrderController` (201 Created)
  - 공통: `GlobalExceptionHandler.resolveValidationErrorCode`에 `menuId → INVALID_MENU_ID` 매핑 추가
- 결과: `./gradlew test` 전체 통과 (16/16, 신규 6건: OrderServiceTest 3 + OrderControllerTest 3)
- 증거(API 샘플, MockMvc 기준):
  - `POST /api/v1/orders` `{"userId":"user-1234","menuId":1}` → `201 {"status":201,"message":"요청이 성공했습니다.","data":{"orderId":5001,"userId":"user-1234","menuId":1,"menuName":"아메리카노","paidAmount":4000,"remainingPoint":6000,"orderedAt":"..."}}`
  - 잔액 부족 → `409 {"status":409,"message":"요청이 실패했습니다.","error":{"code":"INSUFFICIENT_POINT","detail":"보유 포인트가 주문 금액보다 적습니다."}}`
  - `menuId` 누락(검증 실패) → `400 {"status":400,"error":{"code":"INVALID_MENU_ID", ...}}`
- 동시성/트랜잭션 결정:
  - 트랜잭션 경계: `OrderService.order()` 메서드 전체 (메뉴 조회 ~ 포인트 사용 이력 저장까지 하나의 트랜잭션)
  - 락 전략: **비관적 락(Pessimistic Lock, `SELECT ... FOR UPDATE`)** 선택
    - 로컬 `synchronized`는 다수 서버 인스턴스 환경에서 무력화됨 → 제외
    - Redis 분산 락(Redisson)은 별도 인프라 도입이 필요해 이 과제 범위에는 과함 → 제외
    - DB 트랜잭션 레벨 비관적 락은 인스턴스 수와 무관하게 같은 DB 행을 잠그므로, 동일 사용자의 동시 주문 요청이 순차 처리되어 포인트 이중 차감·잔액 음수화를 방지. `UserPointRepository.findByUserIdForUpdate`로 조회 시점부터 트랜잭션 종료까지 해당 사용자의 `user_point` 행을 잠금
- 범위에서 제외한 것:
  - `502 DATA_PLATFORM_SEND_FAILED`(데이터 수집 플랫폼 전송): 실제 외부 API가 없고 문서 설계 메모도 "비동기 처리 권장"이라 이번 구현에서는 제외
