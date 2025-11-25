package com.democode.todo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ValidationErrorResponseDTO {
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> fieldErrors;

    public ValidationErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ValidationErrorResponseDTO(String message, int status, String path, Map<String, String> fieldErrors) {
        this();
        this.message = message;
        this.status = status;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }
}