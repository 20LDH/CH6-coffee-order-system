# 코드 컨벤션

이 문서는 코드 작성 시 지켜야 하는 규칙을 정의한다.
`AGENTS.md`가 "언제 이 문서를 보는지"를 안내하고, 실제 세부 규칙은 여기서 관리한다.

---

## 패키지 구조

도메인 단위로 나눈다. 계층별(controller, service 등) 하위 패키지로 다시 쪼개지 않는다.

```
com.example.ch6coffeeordersystem
├── common      (ApiResponse, 예외 처리 등 공통 요소)
├── menu        (Menu, MenuController, MenuService, MenuRepository, MenuResponse)
├── point       (포인트 관련 전부)
└── order       (주문 관련 전부)
```

새 도메인을 추가할 때도 이 구조를 그대로 따른다 (예: `user` 도메인이 생기면 `user` 패키지 하나에 entity/controller/service/repository/dto를 모두 둔다).

## 클래스 작성 규칙

### Entity
- setter를 만들지 않는다. 상태 변경이 필요하면 의미 있는 이름의 메서드로 노출한다. (예: `charge(int amount)`, `deduct(int amount)`)
- 기본 생성자는 `protected`로 둔다 (JPA 프록시 용도).
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` 사용 (MySQL AUTO_INCREMENT 기준).

### DTO
- 요청/응답 DTO는 `record`로 작성한다.
- 응답 DTO는 엔티티를 직접 반환하지 않고, `static from(Entity entity)` 형태의 정적 팩토리 메서드로 변환한다.

### Repository
- `JpaRepository<Entity, Id>` 상속으로 시작하고, 필요한 쿼리 메서드만 추가한다.

### Service
- 생성자 주입을 사용한다 (필드 주입 `@Autowired` 지양). 의존 필드는 `private final`.
- 조회 전용 서비스는 클래스 레벨에 `@Transactional(readOnly = true)`를 붙인다.
- 쓰기 작업이 있는 메서드는 그 메서드에 `@Transactional`을 따로 붙여 클래스 레벨 설정을 오버라이드한다.
- 포인트 차감, 주문 생성처럼 **동시성이 걸리는 로직**은 다음을 반드시 명시한다.
    - 트랜잭션 경계 (어디서부터 어디까지 하나의 트랜잭션인지)
    - 락 전략 (비관적 락 / 분산 락 등, 왜 그 방식을 선택했는지)

### Controller
- `@RestController` + `@RequestMapping("/api/{도메인}")` 형태를 기본으로 한다.
- 응답은 공통 응답 형식(`ApiResponse`)으로 감싸서 반환한다.
- 요청 DTO는 `@Valid`로 검증한다.

## 공통 응답 형식

성공 응답:
```json
{
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": { }
}
```

실패 응답:
```json
{
  "status": 400,
  "message": "요청이 실패했습니다.",
  "error": {
    "code": "INVALID_AMOUNT",
    "detail": "충전 금액은 0보다 커야 합니다."
  }
}
```

에러 코드는 각 API 문서(`docs/api/*.md`)의 Errors 표에 정의된 것을 사용한다. 새 에러 코드가 필요하면 먼저 해당 API 문서에 추가한 뒤 구현한다.

## 네이밍

- 패키지/클래스: 도메인명을 그대로 사용 (`menu`, `point`, `order`)
- Controller 클래스: `{도메인}Controller`
- Service 클래스: `{도메인}Service`
- Repository 인터페이스: `{도메인}Repository`
- 응답 DTO: `{도메인}Response`, 요청 DTO: `{도메인}{동작}Request` (예: `PointChargeRequest`)