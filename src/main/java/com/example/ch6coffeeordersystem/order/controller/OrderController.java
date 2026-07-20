package com.example.ch6coffeeordersystem.order.controller;

import com.example.ch6coffeeordersystem.common.ApiResponse;
import com.example.ch6coffeeordersystem.order.dto.OrderCreateRequest;
import com.example.ch6coffeeordersystem.order.dto.OrderCreateResponse;
import com.example.ch6coffeeordersystem.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderCreateResponse> order(@Valid @RequestBody OrderCreateRequest request) {
        OrderCreateResponse response = orderService.order(request.userId(), request.menuId());
        return ApiResponse.success(HttpStatus.CREATED.value(), response);
    }
}
