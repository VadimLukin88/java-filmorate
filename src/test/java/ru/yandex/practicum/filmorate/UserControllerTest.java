package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void createController() {       // Создаём новый контроллер перед каждым тестом
        userController = new UserController();
    }

    User fillUserController() {     // Добавляем пользователя с корректными данными
        User newUser = new User(null,"login@yandex.ru", "login", "name", LocalDate.of(1988,9,25));
        User savedUser = null;
        try {
            savedUser = userController.addUser(newUser);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
        return savedUser;
    }

    @Test
    void addCorrectUser() {		// Добавление пользователя с корректно заполненными данными
        User savedUser = fillUserController();
        User loadedUser = userController.getUsers().get(0);

        assertEquals(1, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(savedUser, loadedUser, "Данные пользователя не совпадают!");
    }

    @Test
    void updateCorrectUser() {      //Обновляем данные для пользователя с корректно заполненными данными
        fillUserController();
        User updUser = new User(1,"login@yandex.ru", "NewLogin", "New Name", LocalDate.of(1988,9,25));
        User loadedUser = null;
        try {
            loadedUser = userController.updateUser(updUser);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }

        assertEquals(1, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(loadedUser, updUser, "Данные по пользователю не обновились");
    }

    @Test
    void updateUserWithNullOrWrongId() {      //Обновляем данные для пользователя с некорректным Id
        User savedUser = fillUserController();
        User updUser1 = new User(
                null,
                "login1@yandex.ru",
                "login1",
                "name1",
                LocalDate.of(1988,9,25)
        );
        User updUser2 = new User(
                10,
                "login2@yandex.ru",
                "login2",
                "name2",
                LocalDate.of(1988,9,25)
        );

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser1), "Не получено исключение ");
        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser2), "Не получено исключение ");

        User loadedUser = userController.getUsers().get(0);

        assertEquals(1, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(savedUser, loadedUser, "Данные по фильму изменены");
        assertEquals(ex1.getMessage(), "Error! User ID not found.", "Некорректное сообщение");
        assertEquals(ex2.getMessage(), "Error! User ID not found.", "Некорректное сообщение");
    }

    @Test
    void addUserWithNullOrEmptyEmail() {		// Добавлем пользователя с Null или пустой строкой в поле email
        User newUser1 = new User(null,null, "login1", "name1", LocalDate.of(1988,9,25));
        User newUser2 = new User(null,"", "login2", "name2", LocalDate.of(1988,9,25));

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.addUser(newUser1), "Не получено исключение ");
        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> userController.addUser(newUser2), "Не получено исключение ");

        assertEquals(0, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Invalid e-mail for user.", "Некорректное сообщение");
        assertEquals(ex2.getMessage(), "Error! Invalid e-mail for user.", "Некорректное сообщение");
    }

    @Test
    void updateUserWithNullOrEmptyEmail() {		// Обновляем пользователя с Null или пустой строкой в поле email
        User savedUser = fillUserController();
        User updUser1 = new User(1,null, "login1", "name1", LocalDate.of(1988,9,25));
        User updUser2 = new User(1,"", "login2", "name2", LocalDate.of(1988,9,25));

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser1), "Не получено исключение ");
        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser2), "Не получено исключение ");

        User loadedUser = userController.getUsers().get(0);

        assertEquals(1, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(savedUser, loadedUser, "Данные пользователя изменены!");
        assertEquals(ex1.getMessage(), "Error! Invalid e-mail for user.", "Некорректное сообщение");
        assertEquals(ex2.getMessage(), "Error! Invalid e-mail for user.", "Некорректное сообщение");
    }

    @Test
    void addUserWithEmailNotContainAt() {		// Добавление пользователя. В email отсутствует символ @
        User newUser = new User(null,"just.wrong.email", "login1", "name1", LocalDate.of(1988,9,25));

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.addUser(newUser), "Не получено исключение ");

        assertEquals(0, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Invalid e-mail for user.", "Некорректное сообщение");
    }

    @Test
    void updateUserWithEmailNotContainAt() {		// Обновление пользователя. В email отсутствует символ @
        User savedUser = fillUserController();
        User updUser = new User(1,"just.wrong.email", "NewLogin", "New Name", LocalDate.of(1988,9,25));

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser), "Не получено исключение ");

        User loadedUser = userController.getUsers().get(0);

        assertEquals(1, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(savedUser, loadedUser, "Данные пользователя изменены!");
        assertEquals(ex1.getMessage(), "Error! Invalid e-mail for user.", "Некорректное сообщение");
    }

    @Test
    void addUserWithNullOrEmptyLogin() {        // Добавление пользователя с логином равным null или пустой строке
        User newUser1 = new User(null,"login1@yandex.ru", null, "name1", LocalDate.of(1988,9,25));
        User newUser2 = new User(null,"login2@yandex.ru", "", "name2", LocalDate.of(1988,9,25));

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.addUser(newUser1), "Не получено исключение ");
        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> userController.addUser(newUser2), "Не получено исключение ");

        assertEquals(0, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Invalid user login.", "Некорректное сообщение");
        assertEquals(ex2.getMessage(), "Error! Invalid user login.", "Некорректное сообщение");
    }

    @Test
    void updateUserWithNullOrEmptyLogin() {        // Обновление пользователя с логином равным null или пустой строке
        User savedUser = fillUserController();
        User updUser1 = new User(1,"login1@yandex.ru", null, "name1", LocalDate.of(1988,9,25));
        User updUser2 = new User(1,"login2@yandex.ru", "", "name2", LocalDate.of(1988,9,25));

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser1), "Не получено исключение ");
        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser2), "Не получено исключение ");

        User loadedUser = userController.getUsers().get(0);

        assertEquals(1, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(savedUser, loadedUser, "Данные пользователя изменены");
        assertEquals(ex1.getMessage(), "Error! Invalid user login.", "Некорректное сообщение");
        assertEquals(ex2.getMessage(), "Error! Invalid user login.", "Некорректное сообщение");
    }

    @Test
    void addUserWithLoginContainsWhitespace() {     // Добавлеям пользователя. Логин содержит пробелы
        User newUser = new User(null,"login1@yandex.ru", "login login", "name1", LocalDate.of(1988,9,25));

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.addUser(newUser), "Не получено исключение ");

        assertEquals(0, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Invalid user login.", "Некорректное сообщение");
    }

    @Test
    void updateUserWithLoginContainsWhitespace() {     // Обновляем пользователя. Логин содержит пробелы
        User savedUser = fillUserController();
        User updUser = new User(1,"login1@yandex.ru", "login login", "name1", LocalDate.of(1988,9,25));

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser), "Не получено исключение ");

        User loadedUser = userController.getUsers().get(0);

        assertEquals(1, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(savedUser, loadedUser, "Данные пользователя изменены!");
        assertEquals(ex1.getMessage(), "Error! Invalid user login.", "Некорректное сообщение");
    }

    @Test
    void addUserWithNullOrEmptyName() {     // Имя пользователя null или пустая строка (должен вставиться логин)
        User newUser1 = new User(null,"login1@yandex.ru", "login1", null, LocalDate.of(1988,9,25));
        User newUser2 = new User(null,"login2@yandex.ru", "login2", "", LocalDate.of(1988,9,25));
        User loadedUser1 = null;
        User loadedUser2 = null;
        try {
            loadedUser1 = userController.addUser(newUser1);
            loadedUser2 = userController.addUser(newUser2);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
        assertEquals(2, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(loadedUser1.getName(), loadedUser1.getLogin(), "Имя пользователя не задано. Было null");
        assertEquals(loadedUser2.getName(), loadedUser2.getLogin(), "Имя пользователя не задано. Было пустой строкой");
    }

    @Test
    void addUserFromFuture() {      // Добавление пользователя, с датой рождения из будущего
        User newUser = new User(null,"login1@yandex.ru", "login", "name1", LocalDate.MAX);

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.addUser(newUser), "Не получено исключение ");

        assertEquals(0, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Invalid birthday for user.", "Некорректное сообщение");
    }

    @Test
    void updateUserFromFuture() {      // Обновление пользователя, с датой рождения из будущего
        User savedUser = fillUserController();
        User updUser = new User(1,"login@yandex.ru", "login", "name", LocalDate.MAX);

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> userController.updateUser(updUser), "Не получено исключение ");

        User loadedUser = userController.getUsers().get(0);

        assertEquals(1, userController.getUsers().size(),"Не корректный размер списка");
        assertEquals(savedUser, loadedUser, "Данные пользователя изменены!");
        assertEquals(ex1.getMessage(), "Error! Invalid birthday for user.", "Некорректное сообщение");
    }

}
