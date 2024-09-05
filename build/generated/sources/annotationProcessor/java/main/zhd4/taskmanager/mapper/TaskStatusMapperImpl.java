package zhd4.taskmanager.mapper;

import zhd4.taskmanager.dto.TaskStatusCreateDTO;
import zhd4.taskmanager.dto.TaskStatusDTO;
import zhd4.taskmanager.dto.TaskStatusUpdateDTO;
import zhd4.taskmanager.model.TaskStatus;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-08T18:24:09+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 19.0.2 (Amazon.com Inc.)"
)
@Component
public class TaskStatusMapperImpl extends TaskStatusMapper {

    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    @Override
    public TaskStatus map(TaskStatusCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        TaskStatus taskStatus = new TaskStatus();

        taskStatus.setName( dto.getName() );
        taskStatus.setSlug( dto.getSlug() );

        return taskStatus;
    }

    @Override
    public TaskStatusDTO map(TaskStatus model) {
        if ( model == null ) {
            return null;
        }

        TaskStatusDTO taskStatusDTO = new TaskStatusDTO();

        taskStatusDTO.setId( model.getId() );
        taskStatusDTO.setName( model.getName() );
        taskStatusDTO.setSlug( model.getSlug() );
        taskStatusDTO.setCreatedAt( model.getCreatedAt() );

        return taskStatusDTO;
    }

    @Override
    public void update(TaskStatusUpdateDTO dto, TaskStatus model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            model.setName( jsonNullableMapper.unwrap( dto.getName() ) );
        }
        if ( dto.getSlug() != null ) {
            model.setSlug( jsonNullableMapper.unwrap( dto.getSlug() ) );
        }
    }
}
