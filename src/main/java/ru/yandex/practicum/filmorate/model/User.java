package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<Long> friends = new HashSet<>();

    public void addUsersFriend (Long friendId) { friends.add(friendId); }

    public void removeUsersFriend (Long friendId) { friends.remove(friendId); }
}
