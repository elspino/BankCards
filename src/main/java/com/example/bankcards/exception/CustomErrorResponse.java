package com.example.bankcards.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomErrorResponse {
    private final int statusCode;
    private final String message;
    private final String errorCode;
    private final String origin;
}