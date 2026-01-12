package com.example.taskmanager.controller;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.service.UserTaskService;
import com.example.taskmanager.util.ResUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/task")
public class UserTaskController {

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping("/getUserTaskDetail")
    public ResponseEntity<?> getUserTaskDetail(@RequestParam String username) {
        return userTaskService.getUserTaskDetail(username);
    }

    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO dto) {
        return userTaskService.registerUser(dto);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDTO dto) {
        return userTaskService.updateUser(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        return userTaskService.login(dto);
    }

    @PostMapping(
            value = "/createTask",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createTask(
            @RequestParam("data") String data,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {

        try {
            CreateTaskDTO dto = objectMapper.readValue(data, CreateTaskDTO.class);
            return userTaskService.createTask(dto, file);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    ResUtil.createErrorRes("400", "Invalid request format: " + e.getMessage())
            );
        }
    }

    @PutMapping("/updateTask")
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskDTO dto) {
        return userTaskService.updateTask(dto);
    }

    @DeleteMapping("/deleteTask")
    public ResponseEntity<?> deleteTask(@RequestParam String taskId) {
        return userTaskService.deleteTask(taskId);
    }

}
