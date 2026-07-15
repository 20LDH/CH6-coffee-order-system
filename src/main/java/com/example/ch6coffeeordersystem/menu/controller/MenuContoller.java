package com.example.ch6coffeeordersystem.menu.controller;

package com.example.ch6coffeeordersystem.menu;

import com.example.ch6coffeeordersystem.common.ApiResponse;
import com.example.ch6coffeeordersystem.menu.dto.MenuResponse;
import java.util.List;

import com.example.ch6coffeeordersystem.menu.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ApiResponse<List<MenuResponse>> getMenus() {
        List<MenuResponse> menus = menuService.getMenus();
        return ApiResponse.success(HttpStatus.OK.value(), menus);
    }
}