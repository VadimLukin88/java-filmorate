package ru.yandex.practicum.filmorate;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    /** Всё написанное в этом тесте честно украдено отсюда
     * https://reflectoring.io/spring-boot-web-controller-test/
     *
     * Тут по сути проверяем только валидацию полей класса User.
     * Пытался поднять ApplicationContext полностью и проверить все сценарии как в POSTMAN,
     * но что-то с тестами не заладилось...
     */
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserStorage userStorage;
    @MockBean
    private UserService userService;

    @Test
    public void addCorrectUser() throws Exception {     // Добавляем пользователя с корректными данными
        User newUser = new User(null, "user1@ya.ru", "user1", "name1",LocalDate.of(2000,8,8));
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void addUserWithWrongEmail() throws Exception {   // Добавляем пользователя с некорректным email
        User newUser = new User(null, "user1.ya.ru", "user1", "name1",LocalDate.of(2000,8,8));
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithBlankOrNullLogin() throws Exception{   // Добавляем пользователя с некорректным login
        User newUser = new User(null, "user1@ya.ru", null, "name1",LocalDate.of(2000,8,8));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

        newUser = new User(null, "user1@ya.ru", "", "name1",LocalDate.of(2000,8,8));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserFromFuture() throws Exception {   // Добавляем пользователя с датой рождения в будущем
        User newUser = new User(null, "user1@ya.ru", "user1", "name1", LocalDate.MAX);
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void updateCorrectUser() throws Exception {      //Обновляем данные для пользователя с корректно заполненными данными
        User newUser = new User(null, "user1@ya.ru", "user1", "name1", LocalDate.of(2000, 8, 8));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());

        User updUser = new User(1L,"new_user1@yandex.ru", "new_user1", "new_name1", LocalDate.of(2010,9,25));
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updUser)))
                .andExpect(status().isOk());
    }

    // тесты для update с разными некорректными User дописывать не стал, так как там тот же механизм валидации, что и в create

}
