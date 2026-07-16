package com.example.ch6coffeeordersystem.point.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PointChargeRequest(

        @NotBlank(message = "사용자 식별값은 필수입니다.")
        String userId,

        @Positive(message = "충전 금액은 0보다 커야 합니다.")
        Integer amount
) {
}
