package com.example.ch6coffeeordersystem.menu;

import com.example.ch6coffeeordersystem.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ApiResponse<MenuListResponse> getMenus() {
        MenuListResponse response = MenuListResponse.from(menuService.getMenus());
        return ApiResponse.success(HttpStatus.OK.value(), response);
    }
}
