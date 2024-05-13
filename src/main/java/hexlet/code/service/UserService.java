package hexlet.code.service;

import hexlet.code.component.DefaultUserProperties;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceForbiddenException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DefaultUserProperties defaultUserProperties;

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

        if (taskRepository.findByAssignee(user).isPresent()) {
            throw new ResourceAlreadyExistsException("User related with some task");
        }

        userRepository.delete(user);
    }
}