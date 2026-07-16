package com.example.ch6coffeeordersystem.point.repository;

import com.example.ch6coffeeordersystem.point.entity.UserPoint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    Optional<UserPoint> findByUserId(String userId);
}
