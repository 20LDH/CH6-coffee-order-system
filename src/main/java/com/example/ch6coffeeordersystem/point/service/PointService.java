package com.example.ch6coffeeordersystem.point.service;

import com.example.ch6coffeeordersystem.point.dto.PointChargeResponse;
import com.example.ch6coffeeordersystem.point.entity.PointHistory;
import com.example.ch6coffeeordersystem.point.entity.UserPoint;
import com.example.ch6coffeeordersystem.point.repository.PointHistoryRepository;
import com.example.ch6coffeeordersystem.point.repository.UserPointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public PointService(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository) {
        this.userPointRepository = userPointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    /**
     * 포인트를 충전한다.
     * <p>
     * 트랜잭션 경계: 사용자 포인트 계정 조회(없으면 생성) → balance 증가 → 충전 이력 저장까지
     * 이 메서드 전체가 하나의 트랜잭션이다. 셋 중 하나만 성공하는 상황(예: 이력 저장 실패인데
     * balance만 증가)을 막기 위함이다.
     * <p>
     * 동시성 제어(락)는 이번 구현에서는 적용하지 않는다 (docs/api/point-charge-api.md 설계 메모의
     * 후보 전략 중 아직 선택하지 않음, 추후 별도 작업으로 고도화).
     */
    @Transactional
    public PointChargeResponse charge(String userId, int amount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseGet(() -> new UserPoint(userId));

        userPoint.charge(amount);
        userPointRepository.save(userPoint);

        PointHistory history = PointHistory.ofCharge(userId, amount, userPoint.getBalance());
        pointHistoryRepository.save(history);

        return PointChargeResponse.of(userId, amount, userPoint.getBalance());
    }
}
