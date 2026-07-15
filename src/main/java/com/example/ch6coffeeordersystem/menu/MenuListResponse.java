package com.example.ch6coffeeordersystem.menu;

import java.util.List;

/**
 * GET /api/v1/menus 응답 형태({@code {"menus": [...]}})를
 * {@link com.example.ch6coffeeordersystem.common.ApiResponse}의 data 필드에 그대로 담기 위한 래퍼.
 */
public record MenuListResponse(List<MenuResponse> menus) {

    public static MenuListResponse from(List<MenuResponse> menus) {
        return new MenuListResponse(menus);
    }
}
