package com.example.taskmanager.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserTaskDetailDTO {

   //   User Info
    private String username;
    private String email;
    private String name;
    private String profilePhoto;

    //  Task List
    private List<TaskDetailDTO> tasks;

    @Data
    public static class TaskDetailDTO {
        private String id;
        private String title;
        private String description;
        private String status;
        private LocalDate dueDate;
        private String attachments;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
    }
}
