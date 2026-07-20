package com.example.ch6coffeeordersystem.order.service;

import com.example.ch6coffeeordersystem.common.exception.BusinessException;
import com.example.ch6coffeeordersystem.menu.entity.Menu;
import com.example.ch6coffeeordersystem.menu.repository.MenuRepository;
import com.example.ch6coffeeordersystem.order.dto.OrderCreateResponse;
import com.example.ch6coffeeordersystem.order.entity.Order;
import com.example.ch6coffeeordersystem.order.repository.OrderRepository;
import com.example.ch6coffeeordersystem.point.entity.PointHistory;
import com.example.ch6coffeeordersystem.point.entity.UserPoint;
import com.example.ch6coffeeordersystem.point.repository.PointHistoryRepository;
import com.example.ch6coffeeordersystem.point.repository.UserPointRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public OrderService(OrderRepository orderRepository,
                         MenuRepository menuRepository,
                         UserPointRepository userPointRepository,
                         PointHistoryRepository pointHistoryRepository) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.userPointRepository = userPointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    /**
     * 주문을 생성하고 보유 포인트에서 주문 금액을 차감한다.
     * <p>
     * 트랜잭션 경계: 메뉴 조회 → 포인트 계정 비관적 락 조회 → 잔액 검증/차감 →
     * 주문 저장 → 포인트 사용 이력 저장까지 이 메서드 전체가 하나의 트랜잭션이다.
     * "포인트 차감"과 "주문 생성" 중 하나만 성공하는 상황을 막기 위함이다.
     * <p>
     * 락 전략: 비관적 락(PESSIMISTIC_WRITE, {@code UserPointRepository.findByUserIdForUpdate}).
     * 로컬 synchronized는 다수 서버 인스턴스에서 무력화되지만, DB 행 락은 인스턴스 수와 무관하게
     * 같은 사용자에 대한 동시 주문 요청을 순차 처리해 포인트 이중 차감·음수화를 막는다.
     * Redis 분산 락 대신 DB 락을 선택한 이유는 이 과제가 별도 인프라(Redis) 도입 없이도
     * 단일 DB 트랜잭션 안에서 "차감"과 "주문 생성"을 함께 보장할 수 있어서다.
     * <p>
     * 데이터 수집 플랫폼 전송(502 DATA_PLATFORM_SEND_FAILED)은 이번 구현 범위에서 제외한다.
     */
    @Transactional
    public OrderCreateResponse order(String userId, Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.BAD_REQUEST, "INVALID_MENU_ID", "존재하지 않는 메뉴 ID입니다."));

        UserPoint userPoint = userPointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.CONFLICT, "INSUFFICIENT_POINT", "보유 포인트가 주문 금액보다 적습니다."));

        if (userPoint.getBalance() < menu.getPrice()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT, "INSUFFICIENT_POINT", "보유 포인트가 주문 금액보다 적습니다.");
        }
        userPoint.deduct(menu.getPrice());

        Order order = new Order(userId, menu.getMenuId(), menu.getName(), menu.getPrice());
        orderRepository.save(order);

        PointHistory history = PointHistory.ofUse(userId, menu.getPrice(), userPoint.getBalance(), order.getOrderId());
        pointHistoryRepository.save(history);

        return OrderCreateResponse.of(order, userPoint.getBalance());
    }
}
