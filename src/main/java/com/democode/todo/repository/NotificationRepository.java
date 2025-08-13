package com.democode.todo.repository;

import com.democode.todo.entity.Notifications;
import com.democode.todo.entity.RecipientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notifications,Long> {
    List<Notifications> findByRecipientType(RecipientType recipientType);
    List<Notifications> findByCreatedBy(String createdBy);
    List<Notifications> findByRecipientTypeOrRecipientType(RecipientType type1, RecipientType type2);
}