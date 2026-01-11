package com.example.taskmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterUserDTO {

    private String username;
    private String password;
    private String email;
    private String name;
}
