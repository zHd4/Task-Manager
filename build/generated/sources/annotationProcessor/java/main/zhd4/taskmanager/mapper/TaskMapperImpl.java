package zhd4.taskmanager.mapper;

import zhd4.taskmanager.dto.TaskCreateDTO;
import zhd4.taskmanager.dto.TaskDTO;
import zhd4.taskmanager.dto.TaskUpdateDTO;
import zhd4.taskmanager.model.Task;
import zhd4.taskmanager.model.TaskStatus;
import zhd4.taskmanager.model.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-08T18:24:08+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 19.0.2 (Amazon.com Inc.)"
)
@Component
public class TaskMapperImpl extends TaskMapper {

    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    @Override
    public Task map(TaskCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Task task = new Task();

        task.setName( dto.getTitle() );
        task.setDescription( jsonNullableMapper.unwrap( dto.getContent() ) );
        task.setIndex( dto.getIndex() );

        return task;
    }

    @Override
    public TaskDTO map(Task model) {
        if ( model == null ) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setTitle( model.getName() );
        taskDTO.setContent( model.getDescription() );
        taskDTO.setStatus( modelTaskStatusSlug( model ) );
        taskDTO.setAssigneeId( modelAssigneeId( model ) );
        taskDTO.setId( model.getId() );
        taskDTO.setIndex( model.getIndex() );
        taskDTO.setCreatedAt( model.getCreatedAt() );

        taskDTO.setTaskLabelIds( labelsToLabelIds(model.getLabels()) );

        return taskDTO;
    }

    @Override
    public void update(TaskUpdateDTO dto, Task model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getTitle() != null ) {
            model.setName( jsonNullableMapper.unwrap( dto.getTitle() ) );
        }
        if ( dto.getContent() != null ) {
            model.setDescription( jsonNullableMapper.unwrap( dto.getContent() ) );
        }
        if ( dto.getIndex() != null ) {
            model.setIndex( jsonNullableMapper.unwrap( dto.getIndex() ) );
        }
    }

    private String modelTaskStatusSlug(Task task) {
        if ( task == null ) {
            return null;
        }
        TaskStatus taskStatus = task.getTaskStatus();
        if ( taskStatus == null ) {
            return null;
        }
        String slug = taskStatus.getSlug();
        if ( slug == null ) {
            return null;
        }
        return slug;
    }

    private long modelAssigneeId(Task task) {
        if ( task == null ) {
            return 0L;
        }
        User assignee = task.getAssignee();
        if ( assignee == null ) {
            return 0L;
        }
        long id = assignee.getId();
        return id;
    }
}
