package com.example.bankcards.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final String errorCode;
    private final String origin;
    private final HttpStatus status;

    public ApiException(String errorCode, String message, String origin, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.origin = origin;
        this.status = status;
    }
}