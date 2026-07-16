package com.example.ch6coffeeordersystem.point.controller;

import com.example.ch6coffeeordersystem.common.ApiResponse;
import com.example.ch6coffeeordersystem.point.dto.PointChargeRequest;
import com.example.ch6coffeeordersystem.point.dto.PointChargeResponse;
import com.example.ch6coffeeordersystem.point.service.PointService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping("/charge")
    public ApiResponse<PointChargeResponse> charge(@Valid @RequestBody PointChargeRequest request) {
        PointChargeResponse response = pointService.charge(request.userId(), request.amount());
        return ApiResponse.success(HttpStatus.OK.value(), response);
    }
}
