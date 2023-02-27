package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor     //For JUnit Tests
public class Film {
    private Long id;
    @NotBlank(message = "Имя фильма не должно быть пустым")
    private final String name;
    @Size(min = 1, max = 200, message = "Размер описания фильма не соответсвует ограничениям (минимум - 1, максимум - 200 символов)")
    private final String description;
    @AfterDate(message = "Дата релиза не должна быть раньше 28/12/1895")        // самодельная валидация
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private final Long duration;
    private final Set<Long> likes = new HashSet<>();

    public void addUsersLike (Long userId) { likes.add(userId); }
    public void removeUsersLike (Long userId) { likes.remove(userId); }
}
