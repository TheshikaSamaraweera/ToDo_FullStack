package com.democode.todo.service;

import com.democode.todo.dto.ToDoCreateDTO;
import com.democode.todo.dto.ToDoResponseDTO;
import com.democode.todo.dto.ToDoUpdateDTO;
import com.democode.todo.entity.Status;
import com.democode.todo.entity.TodoList;
import com.democode.todo.repository.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TodoService {

    @Autowired
    private TodoListRepository todoRepository;

    public ToDoResponseDTO createTodo(ToDoCreateDTO createDTO, String createdBy) {
        TodoList todo = new TodoList();
        todo.setTitle(createDTO.getTitle());
        todo.setDescription(createDTO.getDescription());
        todo.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : Status.INCOMPLETE);
        todo.setCreatedBy(createdBy);

        TodoList savedTodo = todoRepository.save(todo);
        return convertToResponseDTO(savedTodo);
    }

    public List<ToDoResponseDTO> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<ToDoResponseDTO> getTodoById(Long id) {
        return todoRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    public List<ToDoResponseDTO> getTodosByUser(String username) {
        return todoRepository.findByCreatedBy(username).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ToDoResponseDTO> getTodosByStatus(Status status) {
        return todoRepository.findByStatus(status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ToDoResponseDTO> getTodosByUserAndStatus(String username, Status status) {
        return todoRepository.findByCreatedByAndStatus(username, status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ToDoResponseDTO> searchTodosByTitle(String title) {
        return todoRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public ToDoResponseDTO updateTodo(Long id, ToDoUpdateDTO updateDTO) {
        TodoList todo = todoRepository.findById(id).orElse(null);
        if (todo != null) {
            if (updateDTO.getTitle() != null) {
                todo.setTitle(updateDTO.getTitle());
            }
            if (updateDTO.getDescription() != null) {
                todo.setDescription(updateDTO.getDescription());
            }
            if (updateDTO.getStatus() != null) {
                todo.setStatus(updateDTO.getStatus());
            }
            TodoList savedTodo = todoRepository.save(todo);
            return convertToResponseDTO(savedTodo);
        }
        return null;
    }

    public boolean deleteTodo(Long id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ToDoResponseDTO convertToResponseDTO(TodoList todo) {
        ToDoResponseDTO dto = new ToDoResponseDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setStatus(todo.getStatus());
        dto.setCreatedBy(todo.getCreatedBy());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        return dto;
    }
}