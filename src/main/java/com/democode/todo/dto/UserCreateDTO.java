package com.democode.todo.dto;
import com.democode.todo.entity.Role;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String username;
    private String password;
    private String email;
    private Role role;
}
