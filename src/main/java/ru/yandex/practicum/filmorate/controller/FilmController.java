package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private int filmId = 0;     // Id фильма
    private final HashMap<Integer, Film> films = new HashMap<>();       // хранилище фильмов в памяти

    /** Временное хранилище для объекта из HTTP запроса.
     *  Добавлено исключительно для того, чтобы при возникновения исключения в теле овета передавался JSON.
     *  Из ТЗ:
     *  "Эндпоинты для создания и обновления данных должны также вернуть созданную или изменённую сущность."
     *  P.S. Был еще вариант запихать поле с дженериком в ValidationException и при выбрасывании исключения
     *  передавать в конструктор объект фильма. Потом в хендлере доставать его обратно.
     *  Но подумал что так наверно делать не стоит...
     */
    private Film filmFromRequest;
    private static final LocalDate BIRTHDAY_OF_CINEMA = LocalDate.of(1895,12,28);   // День рождения кинематографа

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        filmFromRequest = film;     // сохраняем полученный объект в поле класса, чтобы вернуть его в теле ответа при обработке исключения
        validate(film);
        film.setId(++filmId);
        films.put(filmId, film);
        log.info("Film added successfully {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        filmFromRequest = film;
        validate(film);
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new ValidationException("Error! Film ID not found.");
        }
        films.put(film.getId(), film);
        log.info("Film updated successfully {}", film);
        return film;
    }


    /** Добавим немного гибкости.
     * Будем возвращать ResponseEntity, который принимает как параметры тело и статус ответа.
     * Иначе не пройдёт тест в POSTMAN, где при апдейте ожидается 500 или 404 :-(
     * Вместо того, чтобы сравнивать текст message в исключении можно было написать ещё одно исключение
     * и сделать для него отдельный handler.
     * Тогда даже не пришлось бы возвращать ResponseEntity. Можно было бы обойтись аннотациями @ResponseStatus
     * перед хэндлерами.
     * Решил сделать так...
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Film> validExceptionHandler (ValidationException e) {
        log.error(e.getMessage());
        if ("Error! Film ID not found.".equals(e.getMessage())) {
            return new ResponseEntity<>(filmFromRequest, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(filmFromRequest, HttpStatus.BAD_REQUEST);
    }


    private void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Error! Film name invalid.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Error! Film description invalid.");
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMA)) {
            throw new ValidationException("Error! Film release date invalid.");
        }
        if (film.getDuration() != null && film.getDuration() < 0) {
            throw new ValidationException("Error! Film duration invalid.");
        }
    }
}
