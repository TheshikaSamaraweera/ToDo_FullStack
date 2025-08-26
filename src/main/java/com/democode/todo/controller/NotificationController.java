package com.democode.todo.controller;

import com.democode.todo.dto.NotificationCreateDTO;
import com.democode.todo.dto.NotificationResponseDTO;
import com.democode.todo.entity.RecipientType;
import com.democode.todo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody NotificationCreateDTO createDTO,
            @RequestHeader("Created-By") String createdBy) {
        NotificationResponseDTO createdNotification = notificationService.createNotification(createDTO, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        Optional<NotificationResponseDTO> notification = notificationService.getNotificationById(id);
        return notification.map(n -> ResponseEntity.ok(n))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/recipient/{recipientType}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByRecipientType(
            @PathVariable RecipientType recipientType) {
        List<NotificationResponseDTO> notifications =
                notificationService.getNotificationsByRecipientType(recipientType);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByCreator(
            @PathVariable String createdBy) {
        List<NotificationResponseDTO> notifications =
                notificationService.getNotificationsByCreator(createdBy);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/for-role/{role}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsForRole(
            @PathVariable RecipientType role) {
        List<NotificationResponseDTO> notifications =
                notificationService.getNotificationsForRole(role);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        boolean deleted = notificationService.deleteNotification(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}