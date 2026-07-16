package com.example.ch6coffeeordersystem.common;

import com.example.ch6coffeeordersystem.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * API 명세(docs/api/*.md)의 에러코드를 docs/convention.md의 공통 응답 실패 포맷으로 변환한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ApiResponse<Void> response = ApiResponse.fail(e.getStatus().value(), e.getCode(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    /**
     * {@code @Valid} 요청 DTO 검증 실패를 처리한다.
     * 에러코드는 각 API 문서(docs/api/*.md)의 Errors 표를 따르며,
     * 필드명에 매핑된 코드가 있으면 그 코드를, 없으면 공통 코드({@code INVALID_REQUEST})를 사용한다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);
        String detail = fieldError != null ? fieldError.getDefaultMessage() : "요청 값이 유효하지 않습니다.";
        String code = resolveValidationErrorCode(fieldError);
        ApiResponse<Void> response = ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), code, detail);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private String resolveValidationErrorCode(FieldError fieldError) {
        if (fieldError == null) {
            return "INVALID_REQUEST";
        }
        return switch (fieldError.getField()) {
            case "userId" -> "INVALID_USER_ID";
            case "amount" -> "INVALID_AMOUNT";
            default -> "INVALID_REQUEST";
        };
    }
}
