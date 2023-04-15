package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemImpl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private UserStorage userStorage;

    @BeforeEach
    public void setEnvironment() {
        userStorage = new InMemoryUserStorage();
    }

    public void fillUserStorage() {
        userStorage.createUser(new User(null, "user1@ya.ru", "user1", "name1", LocalDate.of(2000, 8, 8)));
        userStorage.createUser(new User(null, "user2@ya.ru", "user2", "name2", LocalDate.of(2001, 9, 9)));
        userStorage.createUser(new User(null, "user3@ya.ru", "user3", "name3", LocalDate.of(2002, 10, 10)));
        userStorage.createUser(new User(null, "user4@ya.ru", "user4", "name4", LocalDate.of(2003, 11, 11)));
    }

    @Test
    public void getAllUsersFromEmptyStorage() {
        assertEquals(userStorage.getAllUsers().size(), 0, "Некорректный размер списка");
    }

    @Test
    public void getAllUsersFromStorage() {
        fillUserStorage();
        assertEquals(userStorage.getAllUsers().size(), 4, "Некорректный размер списка");
    }

    @Test
    public void createUserInEmptyStorage() {
        User savedUser = userStorage.createUser(new User(null, "user1@ya.ru", "user1", "name1", LocalDate.of(2000, 8, 8)));

        assertEquals(userStorage.getAllUsers().size(), 1, "Некорректный размер списка");
        assertEquals(userStorage.getUserById(1L), savedUser, "Сохранён некорректный объект");
    }

    @Test
    public void createUserInStorage() {
        fillUserStorage();
        User savedUser = userStorage.createUser(new User(null, "user5@ya.ru", "user5", "name5", LocalDate.of(2000, 8, 8)));

        assertEquals(userStorage.getAllUsers().size(), 5, "Некорректный размер списка");
        assertEquals(userStorage.getUserById(5L), savedUser, "Сохранён некорректный объект");
    }

    @Test
    public void updateUserInStorage() {
        fillUserStorage();
        User updUser = new User(1L, "new_user1@ya.ru", "new_user1", "new_name1", LocalDate.of(2010, 8, 8));
        User savedUser = userStorage.updateUser(updUser);

        assertEquals(userStorage.getAllUsers().size(), 4, "Некорректный размер списка");
        assertEquals(userStorage.getUserById(1L), savedUser, "Сохранён некорректный объект");
    }

    @Test
    public void deleteUserFromEmptyStorage() {
        assertThrows(DataNotFoundException.class, () -> userStorage.deleteUser(1L), "Не получено исключение");
    }

    @Test
    public void deleteUserFromStorage() {
        fillUserStorage();
        userStorage.deleteUser(1L);

        assertEquals(userStorage.getAllUsers().size(), 3, "Некорректный размер списка");
        assertThrows(ValidationException.class, () -> userStorage.getUserById(null), "Не получено исключение");
    }

    @Test
    public void getUserByIdFromEmptyStorage() {
        assertThrows(ValidationException.class, () -> userStorage.getUserById(null), "Не получено исключение");
    }


    @Test
    public void getUserByIdFromStorage() {
        fillUserStorage();
        User savedUser = userStorage.getUserById(4L);

        assertNotNull(savedUser, "Полученные значение равно null");
        assertEquals(savedUser.getLogin(), "user4", "Получены данные другого пользователя");
    }

    @Test
    public void testIdValidation() {
        fillUserStorage();

        assertThrows(ValidationException.class, () -> userStorage.validateId(null), "Не получено исключение");
        assertThrows(DataNotFoundException.class, () -> userStorage.validateId(9999L), "Не получено исключение");
    }
}