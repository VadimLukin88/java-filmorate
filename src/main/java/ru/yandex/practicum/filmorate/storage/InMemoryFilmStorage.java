package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Long filmId = 0L;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
    @Override
    public Film createFilm(Film film) {     // создание/добавление фильма
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {     // обновление фильма
        validateId(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(Long filmId) {       // удаление фильма
        validateId(filmId);
        films.remove(filmId);
    }

    @Override
    public Film getFilmById(Long filmId) {
        validateId(filmId);
        return films.get(filmId);
    }

    @Override
    public void validateId(Long id) {
        if (id == null) {
            throw new ValidationException("Id фильма равен null");
        }
        if (!films.containsKey(id)) {
            throw new ValidationException(String.format("Фильм с Id = %s не найден", id));
        }
    }
}
