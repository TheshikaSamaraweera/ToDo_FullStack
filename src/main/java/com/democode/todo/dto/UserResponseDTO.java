package com.democode.todo.dto;

import com.democode.todo.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}