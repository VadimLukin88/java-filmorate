package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService (FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {     // ставим like
        userStorage.validateId(userId);
        filmStorage.getFilmById(filmId).addUsersLike(userId);
    }

    public void deleteLike(Long filmId, Long userId) {  // удаляем like
        userStorage.validateId(userId);
        filmStorage.getFilmById(filmId).removeUsersLike(userId);
    }

    public List<Film> getTopFilms(Integer count) {  // ТОП фильмов по кол-ву лайков
        int totalFilms = filmStorage.getAllFilms().size();
        if (count > totalFilms) {
            count = totalFilms;
        }
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .collect(Collectors.toList())
                .subList(0, count);
    }

    // Переносим методы из Film Storage

    public List<Film> getAllFilms() { return filmStorage.getAllFilms(); }

    public Film createFilm(Film film) { return filmStorage.createFilm(film); }

    public Film updateFilm(Film film) { return filmStorage.updateFilm(film); }

    public void deleteFilm(Long filmId) { filmStorage.deleteFilm(filmId); }

    public Film getFilmById(Long filmId) { return filmStorage.getFilmById(filmId); }

}
