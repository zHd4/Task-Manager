package hexlet.code.app.service;

import hexlet.code.app.component.DefaultUserProperties;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceAlreadyExistsException;
import hexlet.code.app.exception.ResourceForbiddenException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.UserUtils;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DefaultUserProperties defaultUserProperties;

    @Bean
    private void createDefaultUser() {
        String email = defaultUserProperties.getEmail();

        if (userRepository.findByEmail(email).isEmpty()) {
            UserCreateDTO userData = new UserCreateDTO();

            userData.setFirstName("");
            userData.setLastName("");
            userData.setEmail(email);

            String password = defaultUserProperties.getPassword();
            String passwordDigest = passwordEncoder.encode(password);

            userData.setPassword(passwordDigest);

            User user = userMapper.map(userData);
            userRepository.save(user);
        }
    }

    private void checkModificationAccess(User user) {
        User currentUser = userUtils.getCurrentUser();
        User admin = userRepository.findByEmail(defaultUserProperties.getEmail()).get();

        if (currentUser == null
                || (user.getId() != currentUser.getId()
                && admin.getId() != currentUser.getId())) {
            throw new ResourceForbiddenException("Access denied");
        }
    }

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO create(UserCreateDTO userData) {
        if (userRepository.findByEmail(userData.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with this email already exists");
        }

        User user = userMapper.map(userData);

        String password = userData.getPassword();
        String passwordDigest = passwordEncoder.encode(password);

        user.setPassword(passwordDigest);
        userRepository.save(user);

        return userMapper.map(user);
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.map(user);
    }

    public UserDTO update(Long id, UserUpdateDTO userData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        checkModificationAccess(user);
        userMapper.update(userData, user);

        JsonNullable<String> passwordNullable = userData.getPassword();

        if (passwordNullable != null && passwordNullable.isPresent()) {
            String password = userData.getPassword().get();
            String passwordDigest = passwordEncoder.encode(password);

            user.setPassword(passwordDigest);
        }

        userRepository.save(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        checkModificationAccess(user);
        userRepository.delete(user);
    }
}
