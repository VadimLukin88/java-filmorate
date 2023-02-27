package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@Validated
//@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final UserStorage userStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
    }



    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable
                        @NotNull(message = "Не указан Id фильма")
                        Long id) {
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getTopFilms(count);
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        Film savedFilm = filmStorage.createFilm(film);
        log.info("Film added successfully {}", savedFilm);
        return savedFilm;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film savedFilm = filmStorage.updateFilm(film);
        log.info("Film updated successfully {}", savedFilm);
        return savedFilm;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Map<String, String> addLikeForFilm(@PathVariable
                                              @NotNull(message = "Не указан Id фильма")
                                              Long id,
                                              @PathVariable
                                              @NotNull(message = "Не указан Id пользователя")
                                              Long userId) {
        filmService.addLike(id, userId);
        return Map.of("Result",
                String.format("Поставили like фильму %s", filmStorage.getFilmById(id).getName()));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Map<String, String> deleteLikeForFilm(@PathVariable
                                                 @NotNull(message = "Не указан Id фильма")
                                                 Long id,
                                                 @PathVariable
                                                 @NotNull(message = "Не указан Id пользователя")
                                                 Long userId) {
        filmService.deleteLike(id, userId);
        return Map.of("Result",
                String.format("Удалили like для фильма %s", filmStorage.getFilmById(id).getName()));
    }

}
