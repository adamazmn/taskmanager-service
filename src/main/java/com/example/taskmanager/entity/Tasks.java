package com.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Tasks {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(columnDefinition = "text")
    private String attachments; // ✅ JSON STRING

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;
}