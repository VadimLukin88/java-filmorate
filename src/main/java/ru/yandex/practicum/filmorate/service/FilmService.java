package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {     // ставим like
        filmStorage.validateId(filmId);
        userStorage.validateId(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {  // удаляем like
        userStorage.validateId(userId);
        filmStorage.validateId(filmId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {  // ТОП фильмов по кол-ву лайков
        return filmStorage.getTopFilms(count);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        filmStorage.validateId(film.getId());
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Long filmId) {
        filmStorage.validateId(filmId);
        filmStorage.deleteFilm(filmId);
    }

    public Film getFilmById(Long filmId) {
        filmStorage.validateId(filmId);
        return filmStorage.getFilmById(filmId);
    }

}
