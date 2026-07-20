package com.example.ch6coffeeordersystem.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
        @NotBlank(message = "사용자 식별값은 필수입니다.") String userId,
        @NotNull(message = "메뉴 ID는 필수입니다.") Long menuId
) {
}
