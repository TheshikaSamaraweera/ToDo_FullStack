package com.democode.todo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class
Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String username;

    private String password;

    private String email;

    private Role role;

    private LocalDateTime createdAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        // Can add updated timestamp if needed later
    }
}
