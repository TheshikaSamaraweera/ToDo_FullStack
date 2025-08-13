package com.democode.todo.dto;
import com.democode.todo.entity.Status;
import lombok.Data;

@Data
public class ToDoUpdateDTO {


    private String title;


    private String description;

    private Status status;
}
