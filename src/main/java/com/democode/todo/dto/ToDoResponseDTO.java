package com.democode.todo.dto;

import ch.qos.logback.core.status.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ToDoResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Status status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
