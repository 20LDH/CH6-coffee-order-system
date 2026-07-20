package com.example.ch6coffeeordersystem.order.dto;

import com.example.ch6coffeeordersystem.order.entity.Order;
import java.time.LocalDateTime;

public record OrderCreateResponse(
        Long orderId,
        String userId,
        Long menuId,
        String menuName,
        int paidAmount,
        int remainingPoint,
        LocalDateTime orderedAt
) {

    public static OrderCreateResponse of(Order order, int remainingPoint) {
        return new OrderCreateResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getMenuId(),
                order.getMenuName(),
                order.getPaidAmount(),
                remainingPoint,
                order.getOrderedAt()
        );
    }
}
