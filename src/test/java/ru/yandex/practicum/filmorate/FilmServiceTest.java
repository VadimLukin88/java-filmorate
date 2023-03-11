package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;

    @BeforeEach
    public void createServiceAndStorage() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
    }

    public void fillFilmStorage() {
        filmStorage.createFilm(new Film(null, "film1", "this is film1", LocalDate.of(1900,1,1), 10L));
        filmStorage.createFilm(new Film(null, "film2", "this is film2", LocalDate.of(1950,1,1), 20L));
        filmStorage.createFilm(new Film(null, "film3", "this is film3", LocalDate.of(2000,1,1), 30L));

        userStorage.createUser(new User(null, "user1@ya.ru", "user1", "name1",LocalDate.of(2000,8,8)));
        userStorage.createUser(new User(null, "user2@ya.ru", "user2", "name2", LocalDate.of(2001,9,9)));
        userStorage.createUser(new User(null, "user3@ya.ru", "user3", "name3", LocalDate.of(2002,10,10)));
        userStorage.createUser(new User(null, "user4@ya.ru", "user4", "name4", LocalDate.of(2003,11,11)));
    }

    @Test
    public void addLikeToFilm() {
        fillFilmStorage();
        filmService.addLike(1L, 1L);
        filmService.addLike(1L, 2L);
        filmService.addLike(2L, 1L);
        filmService.addLike(2L, 2L);

        assertEquals(filmStorage.getFilmById(1L).getLikes().size(), 2,"Некорректный размер списка");
        assertEquals(filmStorage.getFilmById(2L).getLikes().size(), 2,"Некорректный размер списка");

        assertTrue(filmStorage.getFilmById(1L).getLikes().contains(1L), "Нет нужного Id");
        assertTrue(filmStorage.getFilmById(1L).getLikes().contains(2L), "Нет нужного Id");
        assertTrue(filmStorage.getFilmById(2L).getLikes().contains(1L), "Нет нужного Id");
        assertTrue(filmStorage.getFilmById(2L).getLikes().contains(2L), "Нет нужного Id");
    }

    @Test
    public void addLikeWrongId() {
        fillFilmStorage();
        assertThrows(DataNotFoundException.class, () -> filmService.addLike(-1L, 1L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> filmService.addLike(10L, 2L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> filmService.addLike(2L, -1L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> filmService.addLike(2L, 10L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> filmService.addLike(null, 1L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> filmService.addLike(2L, null), "Не получено нужное исключение");

    }

    @Test
    public void deleteLikesFromFilm() {
        fillFilmStorage();
        filmService.addLike(1L, 1L);
        filmService.addLike(1L, 2L);
        filmService.addLike(1L, 3L);

        assertEquals(filmStorage.getFilmById(1L).getLikes().size(), 3,"Некорректный размер списка");
        assertTrue(filmStorage.getFilmById(1L).getLikes().contains(1L), "Нет нужного Id");
        assertTrue(filmStorage.getFilmById(1L).getLikes().contains(2L), "Нет нужного Id");
        assertTrue(filmStorage.getFilmById(1L).getLikes().contains(3L), "Нет нужного Id");

        filmService.deleteLike(1L,1L);
        assertEquals(filmStorage.getFilmById(1L).getLikes().size(), 2,"Некорректный размер списка");
        assertTrue(filmStorage.getFilmById(1L).getLikes().contains(2L), "Нет нужного Id");
        assertTrue(filmStorage.getFilmById(1L).getLikes().contains(3L), "Нет нужного Id");

        filmService.deleteLike(1L,2L);
        filmService.deleteLike(1L,3L);

        assertEquals(filmStorage.getFilmById(1L).getLikes().size(), 0,"Некорректный размер списка");
    }

    @Test
    public void deleteLikesWrongId() {
        fillFilmStorage();
        assertThrows(DataNotFoundException.class, () -> filmService.deleteLike(-1L, 1L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> filmService.deleteLike(10L, 2L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> filmService.deleteLike(2L, -1L), "Не получено нужное исключение");
        assertThrows(DataNotFoundException.class, () -> filmService.deleteLike(2L, 10L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> filmService.deleteLike(null, 1L), "Не получено нужное исключение");
        assertThrows(ValidationException.class, () -> filmService.deleteLike(2L, null), "Не получено нужное исключение");

    }

    @Test
    public void getTopOfFilms() {
        fillFilmStorage();
        filmService.addLike(1L, 1L);

        filmService.addLike(2L, 1L);
        filmService.addLike(2L, 2L);

        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        filmService.addLike(3L, 3L);

        assertEquals(filmService.getTopFilms(1).get(0).getId(), 3, "Некорректное место фильма в топе");

        assertEquals(filmService.getTopFilms(3).get(0).getId(), 3, "Некорректное место фильма в топе");
        assertEquals(filmService.getTopFilms(3).get(1).getId(), 2, "Некорректное место фильма в топе");
        assertEquals(filmService.getTopFilms(3).get(2).getId(), 1, "Некорректное место фильма в топе");

        assertEquals(filmService.getTopFilms(10).size(), 3, "Некорректный размер списка");
    }
}