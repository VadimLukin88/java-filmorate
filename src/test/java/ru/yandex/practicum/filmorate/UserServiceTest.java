package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    public void createServiceAndStorage() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    public void fillUserStorage() {
        userStorage.createUser(new User(null, "user1@ya.ru", "user1", "name1",LocalDate.of(2000,8,8)));
        userStorage.createUser(new User(null, "user2@ya.ru", "user2", "name2", LocalDate.of(2001,9,9)));
        userStorage.createUser(new User(null, "user3@ya.ru", "user3", "name3", LocalDate.of(2002,10,10)));
        userStorage.createUser(new User(null, "user4@ya.ru", "user4", "name4", LocalDate.of(2003,11,11)));
    }

    @Test
    public void addFriendsForUser() {
        fillUserStorage();
        userService.addToFriends(1L,2L);

        assertEquals(userStorage.getUserById(1L).getFriends().size(), 1, "Некорректный размер списка");
        assertEquals(userStorage.getUserById(2L).getFriends().size(), 1, "Некорректный размер списка");

        assertTrue(userStorage.getUserById(1L).getFriends().contains(2L), "Список друзей не содержит нужный Id");
        assertTrue(userStorage.getUserById(2L).getFriends().contains(1L), "Список друзей не содержит нужный Id");
    }

    @Test
    public void addFriendsForUserWithWrongId() {
        fillUserStorage();
        assertThrows(DataNotFoundException.class, () -> userService.addToFriends(10L,2L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> userService.addToFriends(1L,10L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> userService.addToFriends(-1L,-1L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.addToFriends(null,1L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.addToFriends(1L,null), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.addToFriends(null,null), "Не получено нужное исключение");
    }

    @Test
    public void deleteFriendsForUser() {
        fillUserStorage();
        userService.addToFriends(1L,2L);

        assertTrue(userStorage.getUserById(1L).getFriends().contains(2L), "Список друзей не содержит нужный Id");
        assertTrue(userStorage.getUserById(2L).getFriends().contains(1L), "Список друзей не содержит нужный Id");

        userService.deleteFromFriends(1L,2L);

        assertEquals(userStorage.getUserById(1L).getFriends().size(), 0, "Некорректный размер списка");
        assertEquals(userStorage.getUserById(2L).getFriends().size(), 0, "Некорректный размер списка");

        assertFalse(userStorage.getUserById(1L).getFriends().contains(2L), "Список друзей не содержит нужный Id");
        assertFalse(userStorage.getUserById(2L).getFriends().contains(1L), "Список друзей не содержит нужный Id");

    }

    @Test
    public void deleteFriendsForUserWithWrongId() {
        fillUserStorage();
        assertThrows(DataNotFoundException.class, () -> userService.deleteFromFriends(10L,2L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> userService.deleteFromFriends(1L,10L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> userService.deleteFromFriends(-1L,-1L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.deleteFromFriends(null,1L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.deleteFromFriends(1L,null), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.deleteFromFriends(null,null), "Не получено нужное исключение");
    }

    @Test
    public void getAllFriendsOfUser() {
        fillUserStorage();
        userService.addToFriends(1L, 2L);
        userService.addToFriends(1L, 3L);
        userService.addToFriends(1L, 4L);

        assertEquals(userService.getAllUsersFriends(1L).size(), 3, "Некорректный размер списка");
        assertTrue(userStorage.getUserById(1L).getFriends().contains(2L), "Список друзей не содержит нужный Id");
        assertTrue(userStorage.getUserById(1L).getFriends().contains(3L), "Список друзей не содержит нужный Id");
        assertTrue(userStorage.getUserById(1L).getFriends().contains(4L), "Список друзей не содержит нужный Id");
    }

    @Test
    public void getAllFriendsOfUserWithWrongId() {
        assertThrows(ValidationException.class, () -> userService.getAllUsersFriends(null), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> userService.getAllUsersFriends(-1L), "Не получено нужное исключение");
        fillUserStorage();
        assertThrows(ValidationException.class, () -> userService.getAllUsersFriends(null), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> userService.getAllUsersFriends(10L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> userService.getAllUsersFriends(-1L), "Не получено нужное исключение");
    }

    @Test
    public void getMutualFriendsForTwoUsers() {
        fillUserStorage();
        userService.addToFriends(1L, 2L);
        userService.addToFriends(1L, 3L);

        assertEquals(userService.getMutualFriends(2L,3L).size(), 1, "Некорректный размер списка");
        assertEquals(userService.getMutualFriends(2L,3L).get(0).getId(),1L, "Список друзей не содержит нужный Id");
    }

    @Test
    public void getMutualFriendsForTwoUsersWithWrongId() {
        fillUserStorage();
        assertThrows(DataNotFoundException.class, () -> userService.getMutualFriends(-1L,1L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> userService.getMutualFriends(1L, -1L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.getMutualFriends(null, 1L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.getMutualFriends(1L, null), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> userService.getMutualFriends(null, null), "Не получено нужное исключение");
   }

}