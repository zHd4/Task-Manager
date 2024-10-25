package app.taskmanager.controller;

import app.taskmanager.dto.TaskStatusCreateDTO;
import app.taskmanager.dto.TaskStatusDTO;
import app.taskmanager.dto.TaskStatusUpdateDTO;
import app.taskmanager.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    @Autowired
    private TaskStatusService taskStatusService;

    @GetMapping
    ResponseEntity<List<TaskStatusDTO>> index() {
        List<TaskStatusDTO> taskStatuses = taskStatusService.getAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(taskStatuses);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TaskStatusDTO create(@RequestBody @Valid TaskStatusCreateDTO taskStatusData) {
        return taskStatusService.create(taskStatusData);
    }

    @GetMapping("/{id}")
    TaskStatusDTO show(@PathVariable long id) {
        return taskStatusService.findById(id);
    }

    @PutMapping("/{id}")
    TaskStatusDTO update(@PathVariable long id, @RequestBody @Valid TaskStatusUpdateDTO taskStatusData) {
        return taskStatusService.update(id, taskStatusData);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable long id) {
        taskStatusService.delete(id);
    }
}
