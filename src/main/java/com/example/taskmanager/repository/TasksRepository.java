package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, String> {

    List<Tasks> findByUserId(String userId);

    List<Tasks> findByStatus(String status);

    List<Tasks> findByUserIdAndStatus(String userId, String status);
}
