package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
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
    private final MpaRating mpa;
    private final Long rate;
    private SortedSet<Genre> genres = new TreeSet<>(Genre::compareTo);   //список жанров
//    private final List<LikeForFilm> likes = new ArrayList<>();      //Список лайков (id пользователей)
}
