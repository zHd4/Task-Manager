package app.taskmanager.mapper;

import app.taskmanager.dto.TaskCreateDTO;
import app.taskmanager.dto.TaskDTO;
import app.taskmanager.dto.TaskUpdateDTO;
import app.taskmanager.model.Label;
import app.taskmanager.model.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Mapping(target = "assignee", ignore = true)
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(target = "taskLabelIds", expression = "java(labelsToLabelIds(model.getLabels()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    public abstract TaskDTO map(Task model);

    @Mapping(target = "assignee", ignore = true)
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    Set<Long> labelsToLabelIds(Set<Label> labels) {
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
