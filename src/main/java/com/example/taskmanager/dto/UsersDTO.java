package com.example.taskmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsersDTO {

    private String id;
    private String username;
    private String password;
    private String email;
    private String name;

    private String createId;
    private LocalDateTime createDate;

    private String updateId;
    private LocalDateTime updateDate;
}
