package app.taskmanager.service;

import app.taskmanager.exception.ResourceNotFoundException;
import app.taskmanager.repository.LabelRepository;
import app.taskmanager.dto.LabelCreateDTO;
import app.taskmanager.dto.LabelDTO;
import app.taskmanager.dto.LabelUpdateDTO;
import app.taskmanager.mapper.LabelMapper;
import app.taskmanager.model.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        return labelRepository.findAll().stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        return labelMapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO labelData) {
        Label label = labelMapper.map(labelData);

        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(Long id, LabelUpdateDTO labelData) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        labelMapper.update(labelData, label);
        labelRepository.save(label);

        return labelMapper.map(label);
    }

    public void delete(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        labelRepository.delete(label);
    }
}
