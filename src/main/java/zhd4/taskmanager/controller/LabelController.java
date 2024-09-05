package zhd4.taskmanager.controller;

import zhd4.taskmanager.dto.LabelCreateDTO;
import zhd4.taskmanager.dto.LabelDTO;
import zhd4.taskmanager.dto.LabelUpdateDTO;
import zhd4.taskmanager.service.LabelService;
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
@RequestMapping("/api/labels")
public class LabelController {
    @Autowired
    private LabelService labelService;

    @GetMapping
    ResponseEntity<List<LabelDTO>> index() {
        List<LabelDTO> labels = labelService.getAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    @GetMapping("/{id}")
    LabelDTO show(@PathVariable long id) {
        return labelService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    LabelDTO create(@RequestBody @Valid LabelCreateDTO labelData) {
        return labelService.create(labelData);
    }

    @PutMapping("/{id}")
    LabelDTO update(@PathVariable long id, @RequestBody @Valid LabelUpdateDTO labelData) {
        return labelService.update(id, labelData);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable long id) {
        labelService.delete(id);
    }
}
