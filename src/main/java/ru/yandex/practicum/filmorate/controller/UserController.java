package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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

    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        if (isValid(user)) {
            user.setId(++userId);
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь {}", user);
        } else {
            log.debug("Ошибка валидации при добавлении нового пользователя {}", user);
            throw new ValidationException("Ошибка валидации при добавлении нового пользователя!");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        if (isValid(user) && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлены данные пользователя {}", user);
        } else {
            log.debug("Ошибка валидации при обновлении данных пользователя {}", user);
            throw new ValidationException("Ошибка валидации при обновлении данных пользователя!");
        }
        return user;
    }

    private boolean isValid(User user) {
        if (!user.getEmail().contains("@")
                || user.getEmail().isEmpty()
                || user.getBirthday().isAfter(LocalDate.now())) {
            return false;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return true;
    }
}
