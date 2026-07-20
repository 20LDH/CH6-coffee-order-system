package com.example.ch6coffeeordersystem.point.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 포인트 변동(충전/사용) 이력을 append-only로 저장한다. 저장 후에는 값을 변경하지 않으므로
 * 상태 변경 메서드를 두지 않는다.
 */
@Entity
@Table(name = "point_history")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointHistoryType type;

    @Column(nullable = false)
    private int amount;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected PointHistory() {
        // JPA 기본 생성자
    }

    private PointHistory(String userId, PointHistoryType type, int amount, int balanceAfter, Long orderId) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.orderId = orderId;
        this.createdAt = LocalDateTime.now();
    }

    public static PointHistory ofCharge(String userId, int amount, int balanceAfter) {
        return new PointHistory(userId, PointHistoryType.CHARGE, amount, balanceAfter, null);
    }

    public static PointHistory ofUse(String userId, int amount, int balanceAfter, Long orderId) {
        return new PointHistory(userId, PointHistoryType.USE, amount, balanceAfter, orderId);
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public PointHistoryType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public Long getOrderId() {
        return orderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
