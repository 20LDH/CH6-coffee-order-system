package com.example.ch6coffeeordersystem.point.repository;

import com.example.ch6coffeeordersystem.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
