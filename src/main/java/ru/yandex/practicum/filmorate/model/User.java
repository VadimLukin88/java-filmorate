package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor     //For JUnit Tests
public class User {
    private Long id;
    @Email(message = "Некорректный e-mail пользователя")
    private final String email;
    @NotBlank(message = "Логин пользователя не должен быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин пользователя не должен содержать пробелы")
    private final String login;
    private String name;
    @PastOrPresent(message = "Дата рождения пользователя должна быть до текущей даты")
    private final LocalDate birthday;

    public Map<String,Object> toMap(){
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("login", login);
        user.put("name", name);
        user.put("birthday", birthday);
        return user;
    }
}
