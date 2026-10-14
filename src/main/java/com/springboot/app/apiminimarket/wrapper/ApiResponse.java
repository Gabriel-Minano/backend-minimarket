package com.springboot.app.apiminimarket.wrapper;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
}
