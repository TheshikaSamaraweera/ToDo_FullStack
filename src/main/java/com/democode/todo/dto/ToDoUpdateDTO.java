package com.democode.todo.dto;

import ch.qos.logback.core.status.Status;
import lombok.Data;

@Data
public class ToDoUpdateDTO {


    private String title;


    private String description;

    private Status status;
}
