package com.zhd4.taskmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskStatusDTO {
    private long id;
    private String name;
    private String slug;
    private LocalDate createdAt;
}