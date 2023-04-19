package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User createUser(User user); // создание/добавление пользователя

    User updateUser(User user); // обновление данных пользователя

    void deleteUser(Long id);   // удаление пользователя

    User getUserById(Long id);  // получение объекта User по Id

    void validateId(Long id);      // проверка корректности Id

    void addToFriends(Long userId, Long friendId);

    void deleteFromFriends(Long userId, Long friendId);

    List<User> getAllUsersFriends(Long userId);

    List<User> getMutualFriends(Long userId, Long otherId);
}
