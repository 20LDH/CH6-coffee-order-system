package com.example.ch6coffeeordersystem.point.repository;

import com.example.ch6coffeeordersystem.point.entity.UserPoint;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    Optional<UserPoint> findByUserId(String userId);

    /**
     * 주문/결제(docs/api/order-payment-api.md)에서 포인트 차감 시 사용하는 비관적 락 조회.
     * 동일 사용자에 대한 동시 주문 요청이 순차적으로 처리되도록 트랜잭션 종료까지 행을 잠근다.
     * (다수 서버 인스턴스에서도 같은 DB 행을 보므로 DB 락으로 동시성이 보장된다.)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select up from UserPoint up where up.userId = :userId")
    Optional<UserPoint> findByUserIdForUpdate(@Param("userId") String userId);
}
