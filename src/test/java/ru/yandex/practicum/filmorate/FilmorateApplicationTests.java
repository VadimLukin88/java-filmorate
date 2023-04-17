package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InDbImpl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.InDbImpl.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.InDbImpl.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.InDbImpl.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmorateApplicationTests {

    /** Чтобы избежать "пересечения" тестов решил задать явный порядок их выполнения.
     *  Получилось что они зависят друг от друга. Есть подозрение что это не лучшее решение...
     *  В группе обсуждали способ с перезагрузкой контекста для каждого теста.
     *  Но это таааак доооолго!!!
     *  */
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Test
    @Order(1)
    public void testAddNewUser() {
        User userFromRequest1 = new User(null,
                                     "mail1@mail.ru",
                                     "login1",
                                     "name1",
                                           LocalDate.of(2001,1,1));

        User userFromRequest2 = new User(null,
                                       "mail2@mail.ru",
                                       "login2",
                                       "name2",
                                             LocalDate.of(2002,2,2));

        User userFromRequest3 = new User(null,
                                       "mail3@mail.ru",
                                       "login3",
                                       "name3",
                                             LocalDate.of(2003,3,3));

        Optional<User> savedUser1 = Optional.of(userStorage.createUser(userFromRequest1));

        Optional<User> savedUser2 = Optional.of(userStorage.createUser(userFromRequest2));

        Optional<User> savedUser3 = Optional.of(userStorage.createUser(userFromRequest3));

        assertThat(savedUser1).isPresent();
        assertThat(savedUser2).isPresent();
        assertThat(savedUser3).isPresent();
    }

    @Test
    @Order(2)
    public void testFindUserById() {

        Optional<User> userOptional = Optional.of(userStorage.getUserById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
        }

    @Test
    @Order(3)
    public void testGetAllUsers() {
        List<User> allUser = userStorage.getAllUsers();

        assertThat(allUser)
                .isNotNull()
                .hasSize(3);
    }

    @Test
    @Order(4)
    public void testAddToFriends() {
        userStorage.addToFriends(1L, 2L);
        userStorage.addToFriends(1L, 3L);
        userStorage.addToFriends(2L, 3L);
    }

    @Test
    @Order(5)
    public void testGetAllUsersFriends() {
        List<User> allUserFriends = userStorage.getAllUsersFriends(1L);

        assertThat(allUserFriends)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    @Order(6)
    public void testGetMutualFriends() {
        List<User> mutualFriends = userStorage.getMutualFriends(1L, 2L);

        Optional<List<User>> op = Optional.of(mutualFriends);

        assertThat(mutualFriends)
                .isNotNull()
                .hasSize(1);

        assertThat(op)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).first().hasFieldOrPropertyWithValue("id", 3L));
    }

    @Test
    @Order(7)
    public void testCreateFilm() {
        Film filmFromRequest1 = new Film(null,
                                     "film1",
                                 "description1",
                                           LocalDate.of(2001,1,1),
                                   120L,
                                           new MpaRating(1L,null),
                                      1L,
                                           new TreeSet<>());

        Film filmFromRequest2 = new Film(null,
                                      "film2",
                                   "description2",
                                             LocalDate.of(2002,2,2),
                                     20L,
                                             new MpaRating(2L,null),
                                        2L,
                                             new TreeSet<>());

        Film savedFilm1 = filmStorage.createFilm(filmFromRequest1);
        Film savedFilm2 = filmStorage.createFilm(filmFromRequest2);

        assertThat(savedFilm1)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L);
        assertThat(savedFilm2)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    @Order(8)
    public void testUpdateFilm() {

        Film filmFromRequest1 = new Film(1L,
                                      "newFilm1",
                                  "description1",
                                            LocalDate.of(2001,1,1),
                                    120L,
                                            new MpaRating(1L,null),
                                       1L,
                                            new TreeSet<Genre>(Genre::compareTo));

        filmFromRequest1.getGenres().add(new Genre(1L, null));
        filmFromRequest1.getGenres().add(new Genre(2L, null));

        Optional<Film> savedFromRequest1 = Optional.of(filmStorage.updateFilm(filmFromRequest1));

        assertThat(savedFromRequest1)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", "newFilm1"))
                .hasValueSatisfying(film -> assertThat(film.getGenres()).hasSize(2));

    }

    @Test
    @Order(9)
    public void testGetFilmById() {
        Film savedFilm1 = filmStorage.getFilmById(1L);

        assertThat(savedFilm1)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Order(10)
    public void testGetAllFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();

        assertThat(allFilms)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    @Order(11)
    public void testAddLike() {
        filmStorage.addLike(2L, 1L);
        filmStorage.addLike(2L, 2L);
        filmStorage.addLike(2L, 3L);
        Film savedFilm2 = filmStorage.getFilmById(2L);

        assertThat(savedFilm2)
                .isNotNull()
                .hasFieldOrPropertyWithValue("rate", 5L);
    }

    @Test
    @Order(12)
    public void testDeleteLike() {
        filmStorage.deleteLike(2L, 1L);
        Film savedFilm2 = filmStorage.getFilmById(2L);

        assertThat(savedFilm2)
                .isNotNull()
                .hasFieldOrPropertyWithValue("rate", 4L);
    }

    @Test
    @Order(13)
    public void testGetTopFilms() {
        Optional<List<Film>> topFilms = Optional.of(filmStorage.getTopFilms(10));

        assertThat(topFilms)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).first().hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(film -> assertThat(film).last().hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    @Order(14)
    public void testGetAllMpaRating() {
        List<MpaRating> allMpa = mpaDbStorage.getAllMpaRating();

        assertThat(allMpa)
                .isNotNull()
                .hasSize(5);
    }

    @Test
    @Order(15)
    public void testGetMpaRatingById() {
        MpaRating oneMpa = mpaDbStorage.getMpaRatingById(1L);

        assertThat(oneMpa)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @Order(16)
    public void testGetAllGenres() {
        List<Genre> allGenre = genreDbStorage.getAllGenres();

        assertThat(allGenre)
                .isNotNull()
                .hasSize(6);
    }

    @Test
    @Order(17)
    public void testGetGenreById() {
        Genre oneGenre = genreDbStorage.getGenreById(6L);

        assertThat(oneGenre)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Боевик");
    }

}
