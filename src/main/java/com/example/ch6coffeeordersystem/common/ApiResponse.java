package com.example.ch6coffeeordersystem.common;

public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T data;
    private final ErrorDetail error;

    private ApiResponse(int status, String message, T data, ErrorDetail error) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(int status, T data) {
        return new ApiResponse<>(status, "요청이 성공했습니다.", data, null);
    }

    public static <T> ApiResponse<T> fail(int status, String code, String detail) {
        return new ApiResponse<>(status, "요청이 실패했습니다.", null, new ErrorDetail(code, detail));
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public ErrorDetail getError() {
        return error;
    }

    public record ErrorDetail(String code, String detail) {
    }
}
