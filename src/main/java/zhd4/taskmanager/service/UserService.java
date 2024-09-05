package zhd4.taskmanager.service;

import zhd4.taskmanager.component.DefaultUserProperties;
import zhd4.taskmanager.dto.UserCreateDTO;
import zhd4.taskmanager.dto.UserDTO;
import zhd4.taskmanager.dto.UserUpdateDTO;
import zhd4.taskmanager.exception.ResourceAlreadyExistsException;
import zhd4.taskmanager.exception.ResourceNotFoundException;
import zhd4.taskmanager.mapper.UserMapper;
import zhd4.taskmanager.model.User;
import zhd4.taskmanager.repository.TaskRepository;
import zhd4.taskmanager.repository.UserRepository;
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
    private DefaultUserProperties defaultUserProperties;

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

        if (taskRepository.findByAssignee(user).isPresent()) {
            throw new ResourceAlreadyExistsException("User related with some task");
        }

        userRepository.delete(user);
    }
}
