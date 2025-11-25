package com.democode.todo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ErrorResponseDTO {
    private String message;
    private String error;
    private int status;
    private String path;
    private LocalDateTime timestamp;
    private List<String> details;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(String message, String error, int status, String path) {
        this();
        this.message = message;
        this.error = error;
        this.status = status;
        this.path = path;
    }
}