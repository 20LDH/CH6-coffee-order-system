package com.example.ch6coffeeordersystem.menu.dto;

import com.example.ch6coffeeordersystem.menu.entity.Menu;

public record MenuResponse(Long menuId, String name, int price) {

    public static MenuResponse from(Menu menu) {
        return new MenuResponse(menu.getMenuId(), menu.getName(), menu.getPrice());
    }
}
