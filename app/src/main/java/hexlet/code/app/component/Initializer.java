package hexlet.code.app.component;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
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
    private DefaultUserProperties defaultUserProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDefaultUser();
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
}
