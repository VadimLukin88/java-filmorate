package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;
    private static final LocalDate BIRTHDAY_OF_CINEMA = LocalDate.of(1895,12,28);

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        if (isValid(film)) {
            film.setId(++filmId);
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм {}", film);
        } else {
            log.debug("Ошибка валидации при добавлении фильма {}", film);
            throw new ValidationException("Ошибка валидации! Не удалось добавить фильм.");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (isValid(film) && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлены данные по фильму {}", film);
        } else {
            log.debug("Ошибка валидации при обновлении фильма {}", film);
            throw new ValidationException("Ошибка валидации! Не удалось обновить данные фильма.");
        }
        return film;
    }

    private boolean isValid(Film film) {
        if (film.getName().isEmpty()
                || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMA)
                || film.getDuration() < 0) {
            return false;
        } else {
            return true;
        }
    }
}
