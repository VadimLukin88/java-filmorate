package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemImpl.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    public void setEnvironment() {
        filmStorage = new InMemoryFilmStorage();
    }

    public void fillFilmStorage() {
        filmStorage.createFilm(new Film(null,
                                    "film1",
                                "this is film1",
                                           LocalDate.of(1900,1,1),
                                   10L));
        filmStorage.createFilm(new Film(null,
                                    "film2",
                                "this is film2",
                                           LocalDate.of(1950,1,1),
                                   20L));
        filmStorage.createFilm(new Film(null,
                                     "film3",
                                 "this is film3",
                                           LocalDate.of(2000,1,1),
                                   30L));
    }

    @Test
    public void createFilmInEmptyStorage() {
        filmStorage.createFilm(new Film(null,
                                    "film1",
                                "this is film1",
                                           LocalDate.of(1900,1,1),
                                   10L));

        assertEquals(filmStorage.getAllFilms().size(), 1, "Некорректный размер списка");
        assertEquals(filmStorage.getAllFilms().get(0).getId(), 1, "Некорректный Id фильма");
    }

    @Test
    public void createFilmInFilledStorage() {
        fillFilmStorage();
        filmStorage.createFilm(new Film(null,
                                    "film4",
                                "this is film4",
                                           LocalDate.of(2010,1,1),
                                   10L));

        assertEquals(filmStorage.getAllFilms().size(), 4, "Некорректный размер списка");
        assertEquals(filmStorage.getAllFilms().get(3).getId(), 4, "Некорректный Id фильма");
    }

    @Test
    public void updateCorrectFilm() {
        fillFilmStorage();
        filmStorage.updateFilm(new Film(1L,
                                    "film1",
                                "this is new description for film1",
                                           LocalDate.of(2010,1,1),
                                   50L));

        assertEquals(filmStorage.getAllFilms().size(), 3, "Некорректный размер списка");
        assertEquals(filmStorage.getFilmById(1L).getDescription(), "this is new description for film1", "Описание фильма не изменилось");
        assertEquals(filmStorage.getFilmById(1L).getDuration(), 50L, "Длительность фильма не изменилось");

    }

    @Test
    public void deleteFilmFromEmptyStorage() {
        assertThrows(DataNotFoundException.class, () -> filmStorage.deleteFilm(1L), "Не получено исключение");
    }

    @Test
    public void deleteFilmFromStorage() {
        fillFilmStorage();
        filmStorage.deleteFilm(1L);

        assertEquals(filmStorage.getAllFilms().size(), 2, "Некорректный размер списка");
        assertThrows(DataNotFoundException.class, () -> filmStorage.getFilmById(1L), "Не получено исключение");
    }

    @Test
    public void testIdValidation() {
        fillFilmStorage();

        assertThrows(ValidationException.class, () -> filmStorage.validateId(null), "Не получено исключение");
        assertThrows(DataNotFoundException.class, () -> filmStorage.validateId(9999L), "Не получено исключение");
    }
}