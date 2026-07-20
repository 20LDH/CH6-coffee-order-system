package com.example.ch6coffeeordersystem.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void order_createsOrderAndDeductsPoint_whenBalanceIsEnough() {
        // given
        Menu menu = new Menu("아메리카노", 4000);
        ReflectionTestUtils.setField(menu, "menuId", 1L);
        UserPoint userPoint = new UserPoint("user-1234");
        userPoint.charge(10000);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(userPointRepository.findByUserIdForUpdate("user-1234")).thenReturn(Optional.of(userPoint));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderService orderService = new OrderService(
                orderRepository, menuRepository, userPointRepository, pointHistoryRepository);

        // when
        OrderCreateResponse response = orderService.order("user-1234", 1L);

        // then
        assertThat(response.userId()).isEqualTo("user-1234");
        assertThat(response.menuId()).isEqualTo(1L);
        assertThat(response.paidAmount()).isEqualTo(4000);
        assertThat(response.remainingPoint()).isEqualTo(6000);
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    void order_throwsInsufficientPoint_whenBalanceIsNotEnough() {
        // given
        Menu menu = new Menu("아메리카노", 4000);
        UserPoint userPoint = new UserPoint("user-1234");
        userPoint.charge(1000);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(userPointRepository.findByUserIdForUpdate("user-1234")).thenReturn(Optional.of(userPoint));

        OrderService orderService = new OrderService(
                orderRepository, menuRepository, userPointRepository, pointHistoryRepository);

        // when & then
        assertThatThrownBy(() -> orderService.order("user-1234", 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode()).isEqualTo("INSUFFICIENT_POINT"));
    }

    @Test
    void order_throwsInvalidMenuId_whenMenuNotFound() {
        // given
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        OrderService orderService = new OrderService(
                orderRepository, menuRepository, userPointRepository, pointHistoryRepository);

        // when & then
        assertThatThrownBy(() -> orderService.order("user-1234", 999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode()).isEqualTo("INVALID_MENU_ID"));
    }
}
