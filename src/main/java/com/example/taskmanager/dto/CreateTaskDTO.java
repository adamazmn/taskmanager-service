package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateTaskDTO {

    private String title;
    private String description;
    private LocalDate dueDate;
    private String username;
}
