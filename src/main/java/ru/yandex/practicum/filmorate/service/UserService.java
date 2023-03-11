package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addToFriends(Long userId, Long friendId) {        // добавление пользователя в друзья
        userStorage.validateId(friendId);
        userStorage.getUserById(userId).addUsersFriend(friendId);
        userStorage.getUserById(friendId).addUsersFriend(userId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {   // удаление пользователя из друзей
        userStorage.validateId(friendId);
        userStorage.getUserById(userId).removeUsersFriend(friendId);
        userStorage.getUserById(friendId).removeUsersFriend(userId);
    }

    public List<User> getAllUsersFriends(Long userId) {  // получение списка друзей для пользователя
        List<User> friendList = new ArrayList<>();
        for (Long friendId : userStorage.getUserById(userId).getFriends()) {
            friendList.add(userStorage.getUserById(friendId));
        }
        return friendList;
    }

    public List<User> getMutualFriends(Long userId, Long otherId) {  // получение списка общих друзей
        Set<Long> mutualFriendsId = new HashSet<>(userStorage.getUserById(userId).getFriends());

        mutualFriendsId.retainAll(userStorage.getUserById(otherId).getFriends());
        List<User> mutualFriendsList = new ArrayList<>();

        for (Long id : mutualFriendsId) {
            mutualFriendsList.add(userStorage.getUserById(id));
        }
        return mutualFriendsList;
    }

    // Переносим методы из User Storage
    public List<User> getAllUsers() { return userStorage.getAllUsers(); }
    public User createUser(User user) { return userStorage.createUser(user); }
    public User updateUser(User user) { return userStorage.updateUser(user); }
    public void deleteUser(Long userId) { userStorage.deleteUser(userId); }
    public User getUserById(Long userId) { return userStorage.getUserById(userId); }

    }
