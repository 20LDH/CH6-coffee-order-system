package com.example.ch6coffeeordersystem.common.exception;

import org.springframework.http.HttpStatus;

/**
 * API 명세(docs/api/*.md)의 Error Cases에 정의된 에러코드를 표현하는 예외.
 * {@link com.example.ch6coffeeordersystem.common.GlobalExceptionHandler}에서
 * {@link com.example.ch6coffeeordersystem.common.ApiResponse} 실패 포맷으로 변환된다.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
