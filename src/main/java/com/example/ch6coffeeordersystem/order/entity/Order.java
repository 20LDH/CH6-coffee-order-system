package com.example.ch6coffeeordersystem.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 주문 1건 = 메뉴 1개. 주문 시점의 메뉴명·가격을 스냅샷으로 저장해
 * 이후 메뉴 정보가 바뀌어도 과거 주문 내역이 변하지 않도록 한다 (docs/db/erd.md).
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;

    @Column(name = "paid_amount", nullable = false)
    private int paidAmount;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Order() {
        // JPA 기본 생성자
    }

    public Order(String userId, Long menuId, String menuName, int paidAmount) {
        this.userId = userId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.paidAmount = paidAmount;
        this.orderedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
