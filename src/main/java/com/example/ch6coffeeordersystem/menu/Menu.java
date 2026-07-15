package com.example.ch6coffeeordersystem.menu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int price;

    protected Menu() {
        // JPA 기본 생성자
    }

    public Menu(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Long getMenuId() {
        return menuId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
