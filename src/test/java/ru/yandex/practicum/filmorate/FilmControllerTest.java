package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {

    /** Всё написанное ниже честно украдено вот отсюда
     * https://reflectoring.io/spring-boot-test/
     * По сути это повтор тестов в POSTMAN, но пусть будет...
     */
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;


    @Test
    public void createCorrectFilmsAndUsers() throws Exception {      // создаём фильм с корректными данными
        Film film = new Film(null, "film1", "this is film1", LocalDate.of(1980,1,1), 50L);

        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)))
            .andExpect(status().isOk());
    }

    @Test
    public void createFilmWithNullOrBlankName() throws Exception {      // создаём фильм с пустым именем
        Film film = new Film(null, "", "this is film1", LocalDate.of(1980,1,1), 50L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());

        film = new Film(null, null, "this is film1", LocalDate.of(1980,1,1), 50L);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilmWithVeryLongDescription() throws Exception {      // создаём фильм с описанием более 200 символов
        Film film = new Film(null,
                           "name",
                       "blablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablabla",
                                 LocalDate.of(1980,1,1),
                         50L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilmWithIncorrectReleaseDate() throws Exception {      // создаём фильм с датой релиза до 28/12/1895
        Film film = new Film(null,
                "name",
                "blablabla",
                LocalDate.of(1880,1,1),
                50L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilmWithZeroOrNegativeDuration() throws Exception {      // создаём с нулевой или отрицательной длительностью
        Film film = new Film(null,
                "name",
                "blablabla",
                LocalDate.of(1980,1,1),
                0L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());

        film = new Film(null,
                "",
                "blablabla",
                LocalDate.of(1980,1,1),
                -1L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateCorrectFilm() throws Exception {      //Обновляем данные для пользователя с корректно заполненными данными
        Film newFilm = new Film(null,
                "name",
                "blablabla",
                LocalDate.of(1980,1,1),
                10L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isOk());

        Film updFilm = new Film(1L,
                "film1",
                "this is film1",
                LocalDate.of(1980,1,1),
                20L);

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updFilm)))
                .andExpect(status().isOk());
    }

    // тесты для update с разными некорректными Film дописывать не стал, так как там тот же механизм валидации, что и в create

}
