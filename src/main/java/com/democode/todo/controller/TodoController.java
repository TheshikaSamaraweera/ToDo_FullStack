package com.democode.todo.controller;

import com.democode.todo.dto.ToDoCreateDTO;
import com.democode.todo.dto.ToDoResponseDTO;
import com.democode.todo.dto.ToDoUpdateDTO;
import com.democode.todo.entity.Status;
import com.democode.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
@PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ToDoResponseDTO> createTodo(@RequestBody ToDoCreateDTO createDTO,
                                                      @RequestHeader("Created-By") String createdBy) {
        ToDoResponseDTO createdTodo = todoService.createTodo(createDTO, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ToDoResponseDTO>> getAllTodos() {
        List<ToDoResponseDTO> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDoResponseDTO> getTodoById(@PathVariable Long id) {
        Optional<ToDoResponseDTO> todo = todoService.getTodoById(id);
        return todo.map(t -> ResponseEntity.ok(t))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #username") // Admin or owner
    public ResponseEntity<List<ToDoResponseDTO>> getTodosByUser(@PathVariable String username) {
        List<ToDoResponseDTO> todos = todoService.getTodosByUser(username);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ToDoResponseDTO>> getTodosByStatus(@PathVariable Status status) {
        List<ToDoResponseDTO> todos = todoService.getTodosByStatus(status);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/user/{username}/status/{status}")
    public ResponseEntity<List<ToDoResponseDTO>> getTodosByUserAndStatus(
            @PathVariable String username,
            @PathVariable Status status) {
        List<ToDoResponseDTO> todos = todoService.getTodosByUserAndStatus(username, status);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ToDoResponseDTO>> searchTodosByTitle(@RequestParam String title) {
        List<ToDoResponseDTO> todos = todoService.searchTodosByTitle(title);
        return ResponseEntity.ok(todos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDoResponseDTO> updateTodo(@PathVariable Long id,
                                                      @RequestBody ToDoUpdateDTO updateDTO) {
        ToDoResponseDTO updatedTodo = todoService.updateTodo(id, updateDTO);
        if (updatedTodo != null) {
            return ResponseEntity.ok(updatedTodo);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        boolean deleted = todoService.deleteTodo(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
