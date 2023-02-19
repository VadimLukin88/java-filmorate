package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int userId = 0;     // Id пользователя
    private final HashMap<Integer, User> users = new HashMap<>();       // хранилище пользователей в памяти

    private User userFromRequest;

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        userFromRequest = user;
        validate(user);
        user.setId(++userId);
        users.put(userId, user);
        log.info("User added successfully {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        userFromRequest = user;
        validate(user);
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new ValidationException("Error! User ID not found.");
        }
        users.put(user.getId(), user);
        log.info("User updated successfully {}", user);
        return user;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<User> validExceptionHandler (ValidationException e) {
        log.error(e.getMessage());
        if ("Error! User ID not found.".equals(e.getMessage())) {
            return new ResponseEntity<>(userFromRequest, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userFromRequest, HttpStatus.BAD_REQUEST);
    }


    private void validate(User user) throws ValidationException {
        if (user.getEmail() == null
                || !user.getEmail().contains("@")
                || user.getEmail().isEmpty()) {
            throw new ValidationException("Error! Invalid e-mail for user.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Error! Invalid birthday for user.");
        }
        if (user.getLogin() == null
                || user.getLogin().contains(" ")
                || user.getLogin().isEmpty()) {
            throw new ValidationException("Error! Invalid user login.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

}
