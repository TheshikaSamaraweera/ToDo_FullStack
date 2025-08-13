package com.democode.todo.repository;

import com.democode.todo.entity.Status;
import com.democode.todo.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList,Long> {
    List<TodoList> findByCreatedBy(String createdBy);
    List<TodoList> findByStatus(Status status);
    List<TodoList> findByCreatedByAndStatus(String createdBy, Status status);
    List<TodoList> findByTitleContainingIgnoreCase(String title);
}