package com.zhd4.taskmanager.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelCreateDTO {
    @Size(min = 2, max = 1000)
    private String name;
}
