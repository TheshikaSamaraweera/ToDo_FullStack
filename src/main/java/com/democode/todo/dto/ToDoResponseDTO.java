package com.democode.todo.dto;


import com.democode.todo.entity.Status;
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
