package com.example.taskmanager.service;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.entity.Tasks;
import com.example.taskmanager.entity.Users;
import com.example.taskmanager.repository.TasksRepository;
import com.example.taskmanager.repository.UsersRepository;
import com.example.taskmanager.util.ResUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserTaskService {

    private static final String UPLOAD_DIR = "uploads";

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.example.taskmanager.config.JwtUtil jwtUtil;


    @Transactional
    public ResponseEntity<?> getUserTaskDetail(String username) {

        log.info("START getUserTaskDetail | username={}", username);

        try {
            // 1️⃣ Find user
            Users user = usersRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found | username={}", username);
                        return new RuntimeException("User not found");
                    });

            log.info("User found | userId={}, username={}", user.getId(), user.getUsername());

            // 2️⃣ Find tasks
            List<Tasks> tasks = tasksRepository.findByUserId(user.getId());
            log.info("Tasks fetched | userId={}, taskCount={}", user.getId(), tasks.size());

            // 3️⃣ Map to Detail DTO
            UserTaskDetailDTO detailDTO = new UserTaskDetailDTO();
            detailDTO.setUsername(user.getUsername());
            detailDTO.setEmail(user.getEmail());
            detailDTO.setName(user.getName());

            List<UserTaskDetailDTO.TaskDetailDTO> taskDTOList =
                    tasks.stream().map(task -> {
                        UserTaskDetailDTO.TaskDetailDTO taskDTO =
                                new UserTaskDetailDTO.TaskDetailDTO();

                        taskDTO.setTitle(task.getTitle());
                        taskDTO.setDescription(task.getDescription());
                        taskDTO.setStatus(task.getStatus());
                        taskDTO.setDueDate(task.getDueDate());
                        taskDTO.setAttachments(task.getAttachments());
                        taskDTO.setCreatedDate(task.getCreatedDate());

                        return taskDTO;
                    }).collect(Collectors.toList());

            detailDTO.setTasks(taskDTOList);

            log.info("Mapping completed | username={}, taskCount={}", username, taskDTOList.size());

            // 4️⃣ Success response
            log.info("END getUserTaskDetail | status=SUCCESS | username={}", username);

            return ResponseEntity.ok(
                    ResUtil.createSuccessRes(
                            String.valueOf(HttpStatus.OK.value()),
                            "User task detail retrieved",
                            detailDTO
                    )
            );

        } catch (Exception e) {

            log.error("ERROR getUserTaskDetail | username={} | message={}",
                    username, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResUtil.createErrorRes(
                            String.valueOf(HttpStatus.NOT_FOUND.value()),
                            e.getMessage()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<?> registerUser(RegisterUserDTO dto) {

        log.info("START registerUser | username={}", dto.getUsername());

        try {
            // 1️⃣ Check username uniqueness
            if (usersRepository.findByUsername(dto.getUsername()).isPresent()) {
                log.warn("Username already exists | username={}", dto.getUsername());

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ResUtil.createErrorRes(
                                String.valueOf(HttpStatus.CONFLICT.value()),
                                "Username already exists"
                        ));
            }

            // 2️⃣ Create new user
            Users user = new Users();
            user.setId(UUID.randomUUID().toString());
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
            user.setEmail(dto.getEmail());
            user.setName(dto.getName());
            user.setCreateDate(LocalDateTime.now());

            Users savedUser = usersRepository.save(user);

            if (savedUser.getEmail() != null) {
                emailService.sendRegistrationEmail(
                        savedUser.getEmail(),
                        savedUser.getName()
                );
            }

            log.info("User registered successfully | userId={}, username={}",
                    savedUser.getId(), savedUser.getUsername());

            // 3️⃣ Return success
            return ResponseEntity.ok(
                    ResUtil.createSuccessRes(
                            String.valueOf(HttpStatus.OK.value()),
                            "User registered successfully",
                            savedUser
                    )
            );

        } catch (Exception e) {

            log.error("ERROR registerUser | username={} | message={}",
                    dto.getUsername(), e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResUtil.createErrorRes(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            "Failed to register user"
                    ));
        }
    }


    @Transactional
    public ResponseEntity<?> login(LoginDTO dto) {

        log.info("START login | username={}", dto.getUsername());

        try {
            Users user = usersRepository.findByUsername(dto.getUsername())
                    .orElseThrow(() -> new RuntimeException("Invalid username or password"));

            // Simple password check (plaintext)
            if (!user.getPassword().equals(dto.getPassword())) {
                log.warn("Invalid password | username={}", dto.getUsername());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResUtil.createErrorRes(
                                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                                "Invalid username or password"
                        ));
            }

            log.info("Login success | userId={}, username={}",
                    user.getId(), user.getUsername());

            String token = jwtUtil.generateToken(user.getUsername());

            // Return basic user info + token
            java.util.Map<String, Object> responseData = new java.util.HashMap<>();
            responseData.put("username", user.getUsername());
            responseData.put("token", token);

            return ResponseEntity.ok(
                    ResUtil.createSuccessRes(
                            String.valueOf(HttpStatus.OK.value()),
                            "Login successful",
                            responseData
                    )
            );

        } catch (Exception e) {
            log.warn("Login failed | username={}", dto.getUsername());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResUtil.createErrorRes(
                            String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                            "Invalid username or password"
                    ));
        }
    }

    @Transactional
    public ResponseEntity<?> createTask(CreateTaskDTO dto, MultipartFile file) {

        log.info("START createTask | title={}, username={}",
                dto.getTitle(), dto.getUsername());

        // 1️⃣ validate user
        Users user = usersRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ handle file
        List<AttachmentDTO> attachments = new ArrayList<>();

        if (file != null && !file.isEmpty()) {

            File dir = new File("uploads");
            if (!dir.exists()) dir.mkdirs();

            String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads", storedName);

            try {
                Files.write(path, file.getBytes());
            } catch (Exception e) {
                throw new RuntimeException("Failed to store file");
            }

            AttachmentDTO attachment = new AttachmentDTO();
            attachment.setOriginalName(file.getOriginalFilename());
            attachment.setStoredName(storedName);
            attachment.setContentType(file.getContentType());
            attachment.setSize(file.getSize());
            attachment.setPath(path.toString());

            attachments.add(attachment);
        }

        // 3️⃣ serialize attachments
        String attachmentJson;
        try {
            attachmentJson = new ObjectMapper().writeValueAsString(attachments);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize attachments");
        }

        // 4️⃣ save task
        Tasks task = new Tasks();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setStatus("Pending");
        task.setUserId(user.getId());
        task.setAttachments(attachmentJson);
        task.setCreatedDate(LocalDateTime.now());
        task.setUpdatedDate(LocalDateTime.now());

        Tasks saved = tasksRepository.save(task);

        return ResponseEntity.ok(
                ResUtil.createSuccessRes("200", "Task created", saved.getId())
        );
    }

}

