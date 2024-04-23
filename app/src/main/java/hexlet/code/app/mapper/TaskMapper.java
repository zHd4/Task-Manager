package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
public abstract class TaskMapper {
    @Mapping(target = "assignee", ignore = true)
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(target = "taskStatus", ignore = true)
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "name", target = "title")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);
}
