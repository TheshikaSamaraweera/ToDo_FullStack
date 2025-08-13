package com.democode.todo.dto;

import com.democode.todo.entity.RecipientType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private RecipientType recipientType;
    private String createdBy;
    private LocalDateTime createdAt;
}