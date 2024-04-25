package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> getAll() {
        return taskRepository.findAll().stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        return taskMapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO taskData) {
        User assignee = userRepository.findById(taskData.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TaskStatus status = taskStatusRepository.findBySlug(taskData.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));

        Task task = taskMapper.map(taskData);

        task.setAssignee(assignee);
        task.setTaskStatus(status);

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
}
