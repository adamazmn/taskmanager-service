package com.example.taskmanager.exception;

import com.example.taskmanager.util.ResUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Error parsing request body: {}", ex.getMessage(), ex);
        
        String errorMessage = "Invalid request body";
        
        // Check if it's a date format error
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormat = (InvalidFormatException) ex.getCause();
            if (invalidFormat.getTargetType() != null && 
                invalidFormat.getTargetType().getSimpleName().equals("LocalDate")) {
                errorMessage = "Invalid date format. Expected format: YYYY-MM-DD (e.g., 2026-01-15)";
            } else {
                errorMessage = "Invalid format for field: " + invalidFormat.getPath().get(invalidFormat.getPath().size() - 1).getFieldName();
            }
        } else if (ex.getMessage() != null) {
            if (ex.getMessage().contains("JSON parse error") || ex.getMessage().contains("Cannot deserialize")) {
                errorMessage = "Invalid JSON format. Please check your request body structure.";
            } else {
                errorMessage = ex.getMessage();
            }
        }
        
        return ResponseEntity.badRequest().body(
            ResUtil.createErrorRes(String.valueOf(HttpStatus.BAD_REQUEST.value()), errorMessage)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ResUtil.createErrorRes(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), 
                "An unexpected error occurred: " + ex.getMessage())
        );
    }
}

