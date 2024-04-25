package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ResourceAlreadyExistsException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        if (taskStatusRepository.findByName(taskStatusData.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Status already exists");
        }

        if (taskStatusRepository.findBySlug(taskStatusData.getSlug()).isPresent()) {
            throw new ResourceAlreadyExistsException("Status already exists");
        }

        TaskStatus status = taskStatusMapper.map(taskStatusData);

        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }

    public TaskStatusDTO findById(Long id) {
        TaskStatus status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        return taskStatusMapper.map(status);
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO taskStatusData) {
        TaskStatus status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        taskStatusMapper.update(taskStatusData, status);
        taskStatusRepository.save(status);

        return taskStatusMapper.map(status);
    }

    public void delete(Long id) {
        TaskStatus status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        if (taskRepository.findByTaskStatus(status).isPresent()) {
            throw new ResourceAlreadyExistsException("Status related with some task");
        }

        taskStatusRepository.delete(status);
    }
}
