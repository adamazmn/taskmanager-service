package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateTaskDTO {
    private String taskId;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
}
