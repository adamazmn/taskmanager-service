package com.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }
)
@Getter
@Setter
public class Users {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "username", length = 255, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "profile_photo", columnDefinition = "text")
    private String profilePhoto;

    @CreatedBy
    @Column(name = "create_id", length = 36)
    private String createId;

    @CreatedDate
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @LastModifiedBy
    @Column(name = "update_id", length = 36)
    private String updateId;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
