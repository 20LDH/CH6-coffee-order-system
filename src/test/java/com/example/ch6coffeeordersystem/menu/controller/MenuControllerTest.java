package com.example.ch6coffeeordersystem.menu.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ch6coffeeordersystem.menu.dto.MenuResponse;
import com.example.ch6coffeeordersystem.menu.service.MenuService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuService menuService;

    @Test
    void getMenus_returns200WithMenuList() throws Exception {
        // given
        List<MenuResponse> menus = List.of(
                new MenuResponse(1L, "아메리카노", 4000),
                new MenuResponse(2L, "카페라떼", 4500)
        );
        when(menuService.getMenus()).thenReturn(menus);

        // when & then
        mockMvc.perform(get("/api/v1/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(jsonPath("$.data.menus", hasSize(2)))
                .andExpect(jsonPath("$.data.menus[0].menuId").value(1))
                .andExpect(jsonPath("$.data.menus[0].name").value("아메리카노"))
                .andExpect(jsonPath("$.data.menus[0].price").value(4000))
                .andExpect(jsonPath("$.data.menus[1].menuId").value(2))
                .andExpect(jsonPath("$.data.menus[1].name").value("카페라떼"))
                .andExpect(jsonPath("$.data.menus[1].price").value(4500));
    }

    @Test
    void getMenus_returns200WithEmptyList_whenNoMenuExists() throws Exception {
        // given
        when(menuService.getMenus()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menus", hasSize(0)));
    }
}
