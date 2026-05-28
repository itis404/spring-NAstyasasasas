package com.example.tripsplit.exception;

import com.example.tripsplit.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        com.example.tripsplit.controller.ExpenseRestController.class,
        com.example.tripsplit.controller.CurrencyController.class
})
public class RestExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException exception) {
        log.warn("Bad request", exception);
        return ResponseEntity.badRequest().body(new ApiErrorResponse(LocalDateTime.now(), exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleServerError(Exception exception) {
        log.error("REST error", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(LocalDateTime.now(), "Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
