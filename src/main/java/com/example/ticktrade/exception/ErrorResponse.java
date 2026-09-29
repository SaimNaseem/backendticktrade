package com.example.ticktrade.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String message,
        String error,
        int statusCode,
        String path,
        Instant timestamp,
        Map<String,String> fieldErrors) {
}
