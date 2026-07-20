package com.example.ch6coffeeordersystem.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ch6coffeeordersystem.common.exception.BusinessException;
import com.example.ch6coffeeordersystem.order.dto.OrderCreateResponse;
import com.example.ch6coffeeordersystem.order.entity.Order;
import com.example.ch6coffeeordersystem.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void order_returns201WithOrderResult() throws Exception {
        // given
        Order order = new Order("user-1234", 1L, "아메리카노", 4000);
        ReflectionTestUtils.setField(order, "orderId", 5001L);
        when(orderService.order(eq("user-1234"), eq(1L)))
                .thenReturn(OrderCreateResponse.of(order, 6000));

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": "user-1234", "menuId": 1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.orderId").value(5001))
                .andExpect(jsonPath("$.data.userId").value("user-1234"))
                .andExpect(jsonPath("$.data.menuId").value(1))
                .andExpect(jsonPath("$.data.menuName").value("아메리카노"))
                .andExpect(jsonPath("$.data.paidAmount").value(4000))
                .andExpect(jsonPath("$.data.remainingPoint").value(6000));
    }

    @Test
    void order_returns409_whenInsufficientPoint() throws Exception {
        // given
        when(orderService.order(eq("user-1234"), any()))
                .thenThrow(new BusinessException(HttpStatus.CONFLICT, "INSUFFICIENT_POINT", "보유 포인트가 주문 금액보다 적습니다."));

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": "user-1234", "menuId": 1}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error.code").value("INSUFFICIENT_POINT"));
    }

    @Test
    void order_returns400_whenMenuIdIsMissing() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": "user-1234"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error.code").value("INVALID_MENU_ID"));
    }
}
