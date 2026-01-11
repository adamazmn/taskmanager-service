package com.example.taskmanager.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TasksDTO {

    private String id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
    private String attachments;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private String userId;
}
