package com.example.ch6coffeeordersystem.order.repository;

import com.example.ch6coffeeordersystem.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
