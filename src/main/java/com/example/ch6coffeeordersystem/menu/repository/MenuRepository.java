package com.example.ch6coffeeordersystem.menu.repository;

import com.example.ch6coffeeordersystem.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
