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

import java.util.HashSet;
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

        setData(task, taskData);

        taskRepository.save(task);
        return taskMapper.map(task);
    }


    public TaskDTO update(Long id, TaskUpdateDTO taskData) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        setData(task, taskData);

        taskMapper.update(taskData, task);
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        taskRepository.delete(task);
    }

    private void setData(Task task, TaskCreateDTO dto) {
        setData(task, dto.getAssigneeId(), dto.getStatus(), dto.getTaskLabelIds());
    }

    private void setData(Task task, TaskUpdateDTO dto) {
        setData(task, dto.getAssigneeId(), dto.getStatus(), dto.getTaskLabelIds());
    }

    private void setData(Task task,
                         JsonNullable<Long> assigneeId,
                         JsonNullable<String> status,
                         JsonNullable<Set<Long>> labelIds) {
        User newAssignee = null;
        TaskStatus newStatus = null;
        Set<Label> newLabels = new HashSet<>();

        if (assigneeId != null && assigneeId.get() != null && assigneeId.get() != 0) {
            newAssignee = userRepository.findById(assigneeId.get())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        }

        if (status != null && status.get() != null) {
            newStatus = taskStatusRepository.findBySlug(status.get())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        }

        if (labelIds != null && labelIds.get() != null) {
            newLabels = labelIds.get().stream()
                    .map(labelId -> labelRepository.findById(labelId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Label with id=" + labelId + " not found")))
                    .collect(Collectors.toSet());
        }

        task.setAssignee(newAssignee);
        task.setTaskStatus(newStatus);
        task.setLabels(newLabels);
    }
}
