package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addToFriends(Long userId, Long friendId) {        // добавление пользователя в друзья
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        userStorage.addToFriends(userId, friendId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {   // удаление пользователя из друзей
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        userStorage.deleteFromFriends(userId, friendId);
    }

    public List<User> getAllUsersFriends(Long userId) {  // получение списка друзей для пользователя
        userStorage.validateId(userId);
        return userStorage.getAllUsersFriends(userId);
    }

    public List<User> getMutualFriends(Long userId, Long otherId) {  // получение списка общих друзей
        userStorage.validateId(userId);
        userStorage.validateId(otherId);
        return userStorage.getMutualFriends(userId, otherId);
    }

    // Переносим методы из User Storage
    public List<User> getAllUsers() { return userStorage.getAllUsers(); }
    public User createUser(User user) { return userStorage.createUser(user); }
    public User updateUser(User user) {
        userStorage.validateId(user.getId());
        return userStorage.updateUser(user);
    }
    public void deleteUser(Long userId) {
        userStorage.validateId(userId);
        userStorage.deleteUser(userId);
    }
    public User getUserById(Long userId) {
        userStorage.validateId(userId);
        return userStorage.getUserById(userId);
    }

    }
