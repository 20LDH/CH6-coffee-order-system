package com.example.ch6coffeeordersystem.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ch6coffeeordersystem.menu.dto.MenuResponse;
import com.example.ch6coffeeordersystem.menu.entity.Menu;
import com.example.ch6coffeeordersystem.menu.repository.MenuRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Test
    void getMenus_returnsAllMenusAsResponse() {
        // given
        Menu americano = new Menu("아메리카노", 4000);
        Menu latte = new Menu("카페라떼", 4500);
        when(menuRepository.findAll()).thenReturn(List.of(americano, latte));
        MenuService menuService = new MenuService(menuRepository);

        // when
        List<MenuResponse> result = menuService.getMenus();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("아메리카노");
        assertThat(result.get(0).price()).isEqualTo(4000);
        assertThat(result.get(1).name()).isEqualTo("카페라떼");
        assertThat(result.get(1).price()).isEqualTo(4500);
    }

    @Test
    void getMenus_returnsEmptyList_whenNoMenuExists() {
        // given
        when(menuRepository.findAll()).thenReturn(List.of());
        MenuService menuService = new MenuService(menuRepository);

        // when
        List<MenuResponse> result = menuService.getMenus();

        // then
        assertThat(result).isEmpty();
    }
}
