package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
public class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    void createController() {
        filmController = new FilmController();
    }

    Film fillFilmController() {     // Добавляем фильм с корректными данными
        Film newFilm = new Film(
                null,
                "The Good, the Bad and the Ugly",
                "Spaghetti western",
                LocalDate.of(1966,12,23),
                161L
        );
        Film savedFilm = null;

        try {
            savedFilm = filmController.addFilm(newFilm);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
        return savedFilm;
    }

    @Test
    void addCorrectFilm() {		// Добавление фильма с корректно заполненными данными
        Film savedFilm = fillFilmController();
        Film loadedFilm = filmController.getFilms().get(0);

        assertEquals(1, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(savedFilm, loadedFilm, "Данные фильма не совпадают!");
    }

    @Test
    void updateCorrectFilm() {      //Обновляем данные для фильма с корректно заполненными данными
        fillFilmController();
        Film updFilm = new Film(
                1,
                "The Good, the Bad and the Ugly",
                "epic spaghetti western",
                LocalDate.of(1966,12,23),
                177L
        );
        Film loadedFilm = null;

        try {
            loadedFilm = filmController.updateFilm(updFilm);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }

        assertEquals(1, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(loadedFilm, updFilm, "Данные по фильму не обновились");
    }

    @Test
    void updateFilmWithNullOrWrongId() {      //Обновляем данные для фильма с некорректным Id
        Film savedFilm = fillFilmController();
        Film updFilm1 = new Film(
                null,
                "The Good, the Bad and the Ugly",
                "epic spaghetti western",
                LocalDate.of(1966,12,23),
                177L
        );
        Film updFilm2 = new Film(
                10,
                "The Good, the Bad and the Ugly",
                "epic spaghetti western",
                LocalDate.of(1966,12,23),
                177L
        );

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updFilm1), "Не получено исключение ");
        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updFilm2), "Не получено исключение ");

        Film loadedFilm = filmController.getFilms().get(0);

        assertEquals(1, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(savedFilm, loadedFilm, "Данные по фильму изменены");
        assertEquals(ex1.getMessage(), "Error! Film ID not found.", "Некорректное сообщение");
        assertEquals(ex2.getMessage(), "Error! Film ID not found.", "Некорректное сообщение");
    }

    @Test
    void addFilmWithNullOrEmptyName() {     // Добавляем фильм с некорректным именем (null или пустая строка)
        Film newFilm1 = new Film(null, null, "no", LocalDate.of(1970,1,1), 1L);
        Film newFilm2 = new Film(null, "", "no", LocalDate.of(1970,1,1), 1L);

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.addFilm(newFilm1), "Не получено исключение ");
        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> filmController.addFilm(newFilm2), "Не получено исключение ");

        assertEquals(0, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Film name invalid.", "Некорректное сообщение");
        assertEquals(ex2.getMessage(), "Error! Film name invalid.", "Некорректное сообщение");
    }

    @Test
    void updateFilmWithNullOrEmptyName() {      // Обновляем фильм с некорректным именем (null или пустая строка)
        Film savedFilm = fillFilmController();
        Film updFilm1 = new Film(1, null, "no", LocalDate.of(1970,1,1), 1L);
        Film updFilm2 = new Film(1, "", "no", LocalDate.of(1970,1,1), 1L);

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updFilm1), "Не получено исключение ");
        ValidationException ex2 = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updFilm2), "Не получено исключение ");

        Film loadedFilm = filmController.getFilms().get(0);

        assertEquals(1, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(savedFilm, loadedFilm, "Данные фильма изменены");
        assertEquals(ex1.getMessage(), "Error! Film name invalid.", "Некорректное сообщение");
        assertEquals(ex2.getMessage(), "Error! Film name invalid.", "Некорректное сообщение");
    }

    @Test
    void addFilmWithDescriptionLongerThan200Char() { // Добавляем фильм с некорректным описанием (больше 200 символов)
        Film newFilm = new Film(
                null,
                "Name",
                "blablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablavblablablablablablablablabla",
                LocalDate.of(1970,1,1),
                1L
        );

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.addFilm(newFilm), "Не получено исключение ");


        assertEquals(0, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Film description invalid.", "Некорректное сообщение");
    }

    @Test
    void updateFilmWithDescriptionLongerThan200Char() { // Обновляем фильм с некорректным описанием (больше 200 символов)
        Film savedFilm = fillFilmController();
        Film updFilm = new Film(
                1,
                "Name",
                "blablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablavblablablablablablablablabla",
                LocalDate.of(1970,1,1),
                1L
        );

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updFilm), "Не получено исключение ");

        Film loadedFilm = filmController.getFilms().get(0);

        assertEquals(1, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(savedFilm, loadedFilm, "Данные фильма изменены");
        assertEquals(ex1.getMessage(), "Error! Film description invalid.", "Некорректное сообщение");
    }

    @Test
    void addFilmReleasedBeforeCinemaEra() {     // Добавляем фильм с некорректной датой релиза
        Film newFilm = new Film(
                null,
                "Name",
                "blabla",
                LocalDate.of(1895,12,27),
                1L
        );

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.addFilm(newFilm), "Не получено исключение ");

        assertEquals(0, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Film release date invalid.", "Некорректное сообщение");
    }

    @Test
    void updateFilmReleasedBeforeCinemaEra() {      // Обновляем фильм с некорректной датой релиза
        Film savedFilm = fillFilmController();
        Film updFilm = new Film(
                1,
                "Name",
                "blabla",
                LocalDate.of(1895,12,27),
                1L
        );

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updFilm), "Не получено исключение ");

        Film loadedFilm = filmController.getFilms().get(0);

        assertEquals(1, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(savedFilm, loadedFilm, "Данные фильма изменены");
        assertEquals(ex1.getMessage(), "Error! Film release date invalid.", "Некорректное сообщение");
    }

    @Test
    void addFilmWithNegativeDuration() {        // Добавляем фильм с отрицательной длительностью
        Film newFilm = new Film(
                null,
                "Name",
                "blabla",
                LocalDate.of(1970,1,1),
                -1L
        );

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.addFilm(newFilm), "Не получено исключение ");

        assertEquals(0, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(ex1.getMessage(), "Error! Film duration invalid.", "Некорректное сообщение");
    }

    @Test
    void updateFilmWithNegativeDuration() {     // Обновляем фильм с отрицательной длительностью
        Film savedFilm = fillFilmController();
        Film updFilm = new Film(
                1,
                "Name",
                "blabla",
                LocalDate.of(1970,1,1),
                -1L
        );

        ValidationException ex1 = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updFilm), "Не получено исключение ");

        Film loadedFilm = filmController.getFilms().get(0);

        assertEquals(1, filmController.getFilms().size(),"Не корректный размер списка");
        assertEquals(savedFilm, loadedFilm, "Данные фильма изменены");
        assertEquals(ex1.getMessage(), "Error! Film duration invalid.", "Некорректное сообщение");
    }

}
