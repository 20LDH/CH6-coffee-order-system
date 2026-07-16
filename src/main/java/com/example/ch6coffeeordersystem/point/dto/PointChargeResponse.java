package com.example.ch6coffeeordersystem.point.dto;

public record PointChargeResponse(String userId, int chargedAmount, int totalPoint) {

    public static PointChargeResponse of(String userId, int chargedAmount, int totalPoint) {
        return new PointChargeResponse(userId, chargedAmount, totalPoint);
    }
}
