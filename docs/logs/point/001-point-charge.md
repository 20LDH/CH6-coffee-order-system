# 001-point-charge — 포인트 충전 (로그)

## Attempt 1 — 2026-07-16  ✅ PASS
- 시도: `POST /api/v1/points/charge` 구현
  - Entity: `UserPoint`(charge 메서드 노출), `PointHistory`(append-only, `ofCharge` 팩토리), `PointHistoryType` Enum
  - Repository: `UserPointRepository`(findByUserId), `PointHistoryRepository`
  - DTO: `PointChargeRequest`(`@NotBlank userId`, `@Positive amount`), `PointChargeResponse`
  - Service: `PointService.charge()` — UserPoint 조회(없으면 생성) → charge() → PointHistory 저장을 하나의 `@Transactional`로 묶음. 락(동시성 제어)은 이번 구현 범위에서 제외(사용자 지시로 기본 기능만 구현)
  - Controller: `PointController`
  - 공통: 기존 `ApiResponse`에 실패 응답(`fail`, `ErrorDetail`) 추가, `GlobalExceptionHandler`(`BusinessException`, `MethodArgumentNotValidException` 처리) 신규 추가 — point부터 400 에러 케이스가 생겨서 필요
- 결과: `./gradlew test` 전체 통과 (10/10, 신규 5건: PointServiceTest 2 + PointControllerTest 3)
- 증거(API 샘플, MockMvc 기준):
  - `POST /api/v1/points/charge` `{"userId":"user-1234","amount":10000}` → `200 {"status":200,"message":"요청이 성공했습니다.","data":{"userId":"user-1234","chargedAmount":10000,"totalPoint":25000}}`
  - `POST /api/v1/points/charge` `{"userId":"user-1234","amount":0}` → `400 {"status":400,"message":"요청이 실패했습니다.","error":{"code":"INVALID_AMOUNT","detail":"충전 금액은 0보다 커야 합니다."}}`
  - `POST /api/v1/points/charge` `{"userId":"","amount":10000}` → `400 {"status":400,"message":"요청이 실패했습니다.","error":{"code":"INVALID_USER_ID","detail":"사용자 식별값은 필수입니다."}}`
- 동시성/트랜잭션 결정:
  - 트랜잭션 경계: `PointService.charge()` 메서드 전체 (UserPoint 조회/생성 ~ PointHistory 저장까지 하나의 트랜잭션)
  - 락 전략: 적용하지 않음. `docs/api/point-charge-api.md` 설계 메모의 후보(비관적 락/낙관적 락/원자적 UPDATE) 중 아직 선택하지 않은 상태로 남김 — 사용자가 "고도화는 나중에, 기본 기능만" 요청
- 범위에서 제외한 것: `docs/api/point-charge-api.md`의 `USER_NOT_FOUND`(404) 에러코드는 구현하지 않음. `docs/db/erd.md`가 "최초 충전 시 자동 생성" 방식으로 확정되어 있어 사용자 미존재 상황이 발생하지 않음
