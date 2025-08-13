package com.democode.todo.service;

import com.democode.todo.dto.NotificationCreateDTO;
import com.democode.todo.dto.NotificationResponseDTO;
import com.democode.todo.entity.Notifications;
import com.democode.todo.entity.RecipientType;
import com.democode.todo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationResponseDTO createNotification(NotificationCreateDTO createDTO, String createdBy) {
        Notifications notification = new Notifications();
        notification.setMessage(createDTO.getMessage());
        notification.setRecipientType(createDTO.getRecipientType());
        notification.setCreatedBy(createdBy);

        Notifications savedNotification = notificationRepository.save(notification);
        return convertToResponseDTO(savedNotification);
    }

    public List<NotificationResponseDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<NotificationResponseDTO> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    public List<NotificationResponseDTO> getNotificationsByRecipientType(RecipientType recipientType) {
        return notificationRepository.findByRecipientType(recipientType).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> getNotificationsByCreator(String createdBy) {
        return notificationRepository.findByCreatedBy(createdBy).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get notifications for a specific role (including ALL)
    public List<NotificationResponseDTO> getNotificationsForRole(RecipientType userRole) {
        return notificationRepository.findByRecipientTypeOrRecipientType(userRole, RecipientType.ALL).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public boolean deleteNotification(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private NotificationResponseDTO convertToResponseDTO(Notifications notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setRecipientType(notification.getRecipientType());
        dto.setCreatedBy(notification.getCreatedBy());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}