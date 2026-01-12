package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String username; // Used to identify the user
    private String name;
    private String email;
    private String profilePhoto; // Base64 string or URL
    private String password; // Optional: if want to change password
}
