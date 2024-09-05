package com.zhd4.taskmanager.mapper;

import com.zhd4.taskmanager.dto.TaskStatusCreateDTO;
import com.zhd4.taskmanager.dto.TaskStatusDTO;
import com.zhd4.taskmanager.dto.TaskStatusUpdateDTO;
import com.zhd4.taskmanager.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskStatusMapper {
    public abstract TaskStatus map(TaskStatusCreateDTO dto);

    public abstract TaskStatusDTO map(TaskStatus model);

    public abstract void update(TaskStatusUpdateDTO dto, @MappingTarget TaskStatus model);
}
