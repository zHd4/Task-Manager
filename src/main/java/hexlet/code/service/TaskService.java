package hexlet.code.service;

import hexlet.code.component.TaskSpec;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskFilterDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskSpec taskSpec;

    public List<TaskDTO> getAll(TaskFilterDTO filter) {
        Specification<Task> spec = taskSpec.build(filter);

        return taskRepository.findAll(spec).stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        return taskMapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO taskData) {
        Task task = taskMapper.map(taskData);

        if (taskData.getAssigneeId() != null && taskData.getAssigneeId().isPresent()) {
            User assignee = userRepository.findById(taskData.getAssigneeId().get())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            task.setAssignee(assignee);
        }

        if (taskData.getStatus() != null && taskData.getStatus().isPresent()) {
            TaskStatus status = taskStatusRepository.findBySlug(taskData.getStatus().get())
                    .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));

            task.setTaskStatus(status);
        }

        JsonNullable<Set<Long>> labelIds = taskData.getTaskLabelIds();

        if (labelIds != null && labelIds.isPresent()) {
            Set<Label> labels = labelIds.get().stream()
                    .map(id -> labelRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found")))
                    .collect(Collectors.toSet());

            task.setLabels(labels);
        }

        taskRepository.save(task);
        return taskMapper.map(task);
    }


    public TaskDTO update(Long id, TaskUpdateDTO taskData) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        taskMapper.update(taskData, task);
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        taskRepository.delete(task);
    }
}