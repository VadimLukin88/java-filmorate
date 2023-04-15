package ru.yandex.practicum.filmorate.storage.InMemImpl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Long userId = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    @Override
    public User createUser(User user) {     // создание/добавление пользователя
        if (user.getName() == null || user.getName().isEmpty()) {   // требование из ТЗ_9 - если NAME не заполнено, то записываем в NAME значение из LOGIN
            user.setName(user.getLogin());
        }
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {     // обновление данных пользователя
        validateId(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {       // удаление пользователя
        validateId(userId);
        users.remove(userId);
    }

    @Override
    public User getUserById(Long userId) {      // получение User по Id
        validateId(userId);
        return users.get(userId);
    }

    @Override
    public void validateId(Long id) {
        if (id == null) {
            throw new ValidationException("Id пользователя равно null");
        }
        if (!users.containsKey(id)) {
            throw new DataNotFoundException(String.format("Пользователь с Id=%s не найден", id));
        }
    }

    @Override
    public void addToFriends(Long userId, Long friendId) {}

    @Override
    public void deleteFromFriends(Long userId, Long friendId) {}

    @Override
    public List<User> getAllUsersFriends(Long userId) { return null; }

    @Override
    public List<User> getMutualFriends(Long userId, Long otherId) { return null; }

}
