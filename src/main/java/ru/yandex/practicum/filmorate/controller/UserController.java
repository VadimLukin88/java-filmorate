package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
//@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController (UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable
                        @NotNull(message = "Не указан Id пользователя")
                        Long id)  {
        return userService.getUserById(id);
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
        User savedUser = userService.createUser(user);
        log.info("User added successfully {}", user);
        return savedUser;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        User savedUser = userService.updateUser(user);
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
                                  userService.getUserById(id).getName(),
                                  userService.getUserById(friendId).getName()));
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
                                   userService.getUserById(id).getName(),
                                   userService.getUserById(friendId).getName()));
    }

}
