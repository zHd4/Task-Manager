package hexlet.code.app.component;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Initializer implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private DefaultUserProperties defaultUserProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDefaultUser();
        createDefaultTaskStatuses();
    }

    private void createDefaultUser() {
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

    private void createDefaultTaskStatuses() {
        List<String> slugs = List.of("draft", "to_review", "to_be_fixed", "to_publish", "published");

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
