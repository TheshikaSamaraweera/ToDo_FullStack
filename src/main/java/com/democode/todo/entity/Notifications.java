package com.democode.todo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String message;

    @Enumerated(EnumType.STRING)
    private RecipientType recipientType;

    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

}
