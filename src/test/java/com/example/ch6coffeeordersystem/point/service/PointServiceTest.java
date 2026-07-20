package com.example.ch6coffeeordersystem.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ch6coffeeordersystem.point.dto.PointChargeResponse;
import com.example.ch6coffeeordersystem.point.entity.PointHistory;
import com.example.ch6coffeeordersystem.point.entity.UserPoint;
import com.example.ch6coffeeordersystem.point.repository.PointHistoryRepository;
import com.example.ch6coffeeordersystem.point.repository.UserPointRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void charge_createsNewUserPoint_whenFirstCharge() {
        // given
        when(userPointRepository.findByUserId("user-1234")).thenReturn(Optional.empty());
        when(userPointRepository.save(any(UserPoint.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PointService pointService = new PointService(userPointRepository, pointHistoryRepository);

        // when
        PointChargeResponse response = pointService.charge("user-1234", 10000);

        // then
        assertThat(response.userId()).isEqualTo("user-1234");
        assertThat(response.chargedAmount()).isEqualTo(10000);
        assertThat(response.totalPoint()).isEqualTo(10000);
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    void charge_addsToExistingBalance_whenUserAlreadyExists() {
        // given
        UserPoint existing = new UserPoint("user-1234");
        existing.charge(15000);
        when(userPointRepository.findByUserId("user-1234")).thenReturn(Optional.of(existing));
        when(userPointRepository.save(any(UserPoint.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PointService pointService = new PointService(userPointRepository, pointHistoryRepository);

        // when
        PointChargeResponse response = pointService.charge("user-1234", 10000);

        // then
        assertThat(response.chargedAmount()).isEqualTo(10000);
        assertThat(response.totalPoint()).isEqualTo(25000);
    }
}
