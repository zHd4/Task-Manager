package com.zhd4.taskmanager.repository;

import com.zhd4.taskmanager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    Optional<TaskStatus> findByName(String name);
    Optional<TaskStatus> findBySlug(String slug);
}