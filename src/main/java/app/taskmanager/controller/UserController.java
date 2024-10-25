package app.taskmanager.controller;

import app.taskmanager.dto.UserCreateDTO;
import app.taskmanager.dto.UserDTO;
import app.taskmanager.dto.UserUpdateDTO;
import app.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> users = userService.getAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @GetMapping("/{id}")
    UserDTO show(@PathVariable long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO create(@RequestBody @Valid UserCreateDTO userData) {
        return userService.create(userData);
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "@userService.findById(#id).getEmail() == authentication.name")
    UserDTO update(@PathVariable long id, @RequestBody @Valid UserUpdateDTO userData) {
        return userService.update(id, userData);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(value = "@userService.findById(#id).getEmail() == authentication.name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable long id) {
        userService.delete(id);
    }
}
