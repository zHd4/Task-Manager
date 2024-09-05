package zhd4.taskmanager.component;

import zhd4.taskmanager.dto.LabelCreateDTO;
import zhd4.taskmanager.dto.TaskStatusCreateDTO;
import zhd4.taskmanager.dto.UserCreateDTO;
import zhd4.taskmanager.mapper.LabelMapper;
import zhd4.taskmanager.mapper.TaskStatusMapper;
import zhd4.taskmanager.mapper.UserMapper;
import zhd4.taskmanager.model.User;
import zhd4.taskmanager.repository.LabelRepository;
import zhd4.taskmanager.repository.TaskStatusRepository;
import zhd4.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Initializer implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private DefaultUserProperties defaultUserProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDefaultUser();
        createDefaultTaskStatuses();
        createDefaultLabels();
    }

    private void createDefaultUser() {
        if (userRepository.findAll().isEmpty()) {
            String email = defaultUserProperties.getEmail();

            if (userRepository.findByEmail(email).isEmpty()) {
                UserCreateDTO userData = new UserCreateDTO();

                userData.setFirstName("John");
                userData.setLastName("Doe");
                userData.setEmail(email);

                String password = defaultUserProperties.getPassword();
                String passwordDigest = passwordEncoder.encode(password);

                userData.setPassword(passwordDigest);

                User user = userMapper.map(userData);
                userRepository.save(user);
            }
        }
    }

    private void createDefaultTaskStatuses() {
        if (taskStatusRepository.findAll().isEmpty()) {
            TaskStatusCreateDTO draftDTO = new TaskStatusCreateDTO();

            draftDTO.setName("Draft");
            draftDTO.setSlug("draft");

            TaskStatusCreateDTO toViewDTO = new TaskStatusCreateDTO();

            toViewDTO.setName("To view");
            toViewDTO.setSlug("to_review");

            TaskStatusCreateDTO toBeFixedDTO = new TaskStatusCreateDTO();

            toBeFixedDTO.setName("To be fixed");
            toBeFixedDTO.setSlug("to_be_fixed");

            TaskStatusCreateDTO toPublishDTO = new TaskStatusCreateDTO();

            toPublishDTO.setName("To publish");
            toPublishDTO.setSlug("to_publish");

            TaskStatusCreateDTO publishedDTO = new TaskStatusCreateDTO();

            publishedDTO.setName("Published");
            publishedDTO.setSlug("published");

            taskStatusRepository.save(taskStatusMapper.map(draftDTO));
            taskStatusRepository.save(taskStatusMapper.map(toViewDTO));
            taskStatusRepository.save(taskStatusMapper.map(toBeFixedDTO));
            taskStatusRepository.save(taskStatusMapper.map(toPublishDTO));
            taskStatusRepository.save(taskStatusMapper.map(publishedDTO));
        }
    }

    private void createDefaultLabels() {
        if (labelRepository.findAll().isEmpty()) {
            LabelCreateDTO bugLabelDTO = new LabelCreateDTO();
            bugLabelDTO.setName("bug");

            LabelCreateDTO featureLabelDTO = new LabelCreateDTO();
            featureLabelDTO.setName("feature");

            labelRepository.save(labelMapper.map(bugLabelDTO));
            labelRepository.save(labelMapper.map(featureLabelDTO));
        }
    }
}
