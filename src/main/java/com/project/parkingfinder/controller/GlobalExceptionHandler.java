package com.project.parkingfinder.controller;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@ControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        System.out.println("Last Error: " + e.getMessage()); // In ra lỗi cuối cùng
        ErrorResponse errorResponse = new ErrorResponse(new Date(), e.getMessage(), request.getDescription(false));
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Xử lý MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        System.out.println("Last Error: " + ex.getMessage()); // In ra lỗi cuối cùng
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // Xử lý RuntimeException
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, WebRequest request) {
        System.out.println("Last Error: " + e.getMessage()); // In ra lỗi cuối cùng
        ErrorResponse errorResponse = new ErrorResponse(new Date(), "Internal server error", request.getDescription(false));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Xử lý các ngoại lệ không xác định
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e, WebRequest request) {
        System.out.println("Last Error: " + e.getMessage()); // In ra lỗi cuối cùng
        ErrorResponse errorResponse = new ErrorResponse(new Date(), "An unexpected error occurred", request.getDescription(false));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Định nghĩa lớp ErrorResponse để chứa thông tin lỗi
    @Getter
    public static class ErrorResponse {
        private Date timestamp;
        private String message;
        private String details;

        public ErrorResponse(Date timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }
    }
}
