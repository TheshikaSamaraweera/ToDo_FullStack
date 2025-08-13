package com.democode.todo.dto;

import com.democode.todo.entity.RecipientType;
import lombok.Data;

@Data
public class NotificationCreateDTO {
    private String message;
    private RecipientType recipientType;
}