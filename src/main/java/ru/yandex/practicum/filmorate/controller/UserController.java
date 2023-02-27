package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
//@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    public UserController (UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable
                        @NotNull(message = "Не указан Id пользователя")
                        Long id)  {
        return userStorage.getUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriendsList(@PathVariable
                                         @NotNull(message = "Не указан Id пользователя")
                                         Long id) {
        return userService.getAllUsersFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getMutualFriendsList(@PathVariable
                                           @NotNull(message = "Не указан Id пользователя")
                                           Long id,
                                           @PathVariable
                                           @NotNull(message = "Не указан Id друга")
                                           Long otherId) {
        return userService.getMutualFriends(id, otherId);
    }


    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        User savedUser = userStorage.createUser(user);
        log.info("User added successfully {}", user);
        return savedUser;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        User savedUser = userStorage.updateUser(user);
        log.info("User updated successfully {}", user);
        return savedUser;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public Map<String, String> addUserToFriends(@PathVariable
                                                @NotNull(message = "Не указан Id пользователя")
                                                Long id,
                                                @PathVariable
                                                @NotNull(message = "Не указан Id друга")
                                                Long friendId) {
        userService.addToFriends(id, friendId);
        return Map.of("Result",
                           String.format("%s добавил в друзья %s",
                                userStorage.getUserById(id).getName(),
                                userStorage.getUserById(friendId).getName()));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public Map<String, String> deleteUserFromFriends(@PathVariable
                                                     @NotNull(message = "Не указан Id пользователя")
                                                     Long id,
                                                     @PathVariable
                                                     @NotNull(message = "Не указан Id друга")
                                                     Long friendId) {
        userService.deleteFromFriends(id, friendId);
        return Map.of("Result",
                           String.format("%s удалил из друзей %s",
                                   userStorage.getUserById(id).getName(),
                                   userStorage.getUserById(friendId).getName()));
    }

}
