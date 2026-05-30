package com.example.tripsplit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private int status;
}
