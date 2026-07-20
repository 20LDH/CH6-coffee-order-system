package com.example.ch6coffeeordersystem.point.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_point")
public class UserPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(nullable = false)
    private int balance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected UserPoint() {
        // JPA 기본 생성자
    }

    public UserPoint(String userId) {
        this.userId = userId;
        this.balance = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void charge(int amount) {
        this.balance += amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주문/결제 시 포인트를 차감한다. 보유 포인트가 부족하면 차감하지 않고 예외를 던진다.
     * 호출 전에 비관적 락({@code UserPointRepository.findByUserIdForUpdate})으로 조회한 상태여야
     * 동시 주문 시 잔액이 음수가 되는 상황을 막을 수 있다.
     */
    public void deduct(int amount) {
        if (this.balance < amount) {
            throw new IllegalStateException("보유 포인트가 부족합니다.");
        }
        this.balance -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public int getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
