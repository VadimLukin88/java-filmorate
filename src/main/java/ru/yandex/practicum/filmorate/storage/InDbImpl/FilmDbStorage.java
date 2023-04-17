package ru.yandex.practicum.filmorate.storage.InDbImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        String filmSql = new StringBuilder()
                .append("SELECT f.ID f_id, f.NAME f_name, f.description f_description, f.releasedate f_date, ")
                .append("f.duration f_duration, f.rate f_rate, mr.id mr_id, mr.name mr_name ")
                .append("FROM films f ")
                .append("JOIN mpa_rating mr ON f.mpa_id = mr.id;")
                .toString();

        String genreSgl = new StringBuilder()
                .append("SELECT fg.genre_id g_id, g.name FROM films_genre fg ")
                .append("JOIN genre g ON fg.genre_id = g.id ")
                .append("WHERE fg.film_id = ? ")
                .append("ORDER BY g_id;")
                .toString();

        List<Film> allFilms = jdbcTemplate.query(filmSql, filmRowMapper());

        for (Film film : allFilms) {
            film.getGenres().addAll(jdbcTemplate.query(genreSgl, genreRowMapper(), film.getId()));
        }
        return allFilms;
    }

    @Override
    public Film createFilm(Film film) {
        final String sql = "INSERT INTO films (name, description, releaseDate, duration, mpa_id, rate) VALUES(?,?,?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            Long rate = 0L;

            if (film.getRate() != null) {
                rate = film.getRate();
            }
            stmt.setLong(6, rate);
            return stmt;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();

        film.setId(id);

        if (film.getGenres() == null) {
            return film;
        }
        String genreSql = "INSERT INTO films_genre(film_id, genre_id) VALUES(?, ?);";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(genreSql, id, genre.getId());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = new StringBuilder()
                .append("UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, ")
                .append("mpa_id = ?, rate = ? ")
                .append("WHERE id = ?;")
                .toString();

        jdbcTemplate.update(sql,
                            film.getName(),
                            film.getDescription(),
                            film.getReleaseDate(),
                            film.getDuration(),
                            film.getMpa().getId(),
                            film.getRate(),
                            film.getId());
        if (film.getGenres() == null) {
            return film;
        }
        jdbcTemplate.update("DELETE films_genre WHERE film_id = ?;", film.getId());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO films_genre VALUES (?,?);", film.getId(), genre.getId());
        }
        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        String sql = "DELETE FROM films WHERE id = ?;";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT f.ID f_id, f.NAME f_name, f.DESCRIPTION f_description, f.RELEASEDATE f_date, f.DURATION f_duration, f.RATE f_rate, mr.ID mr_id, mr.NAME mr_name "
                + "FROM  FILMS f "
                + "JOIN MPA_RATING mr ON f.MPA_ID = mr.ID "
                + "WHERE f.ID = ?";

        String genreSgl = "SELECT fg.GENRE_ID g_id, g.NAME FROM films_genre fg JOIN GENRE g  ON fg.GENRE_ID = g.ID WHERE fg.FILM_ID = ? ORDER BY g_id;";

        Film film = jdbcTemplate.queryForObject(sql, filmRowMapper(), id);
        film.getGenres().addAll(jdbcTemplate.query(genreSgl, genreRowMapper(), id));
        return film;
    }

    @Override
    public void validateId(Long id) {
        String sql = "SELECT TOP 1 1 FROM films f WHERE f.id = ?;";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("Фильм с Id = %s не найден", id);

            log.error(message);
            throw new DataNotFoundException(message);
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String likeSql = "INSERT INTO films_likes VALUES (?,?);";
        String rateSql = "UPDATE films SET rate = rate+1 WHERE id = ?;";
        jdbcTemplate.update(likeSql, filmId, userId);
        jdbcTemplate.update(rateSql, filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String likeSql = "DELETE FROM films_likes WHERE user_id = ?; ";
        String rateSql = "UPDATE films SET rate = rate-1 WHERE id = ?;";
        jdbcTemplate.update(likeSql, userId);
        jdbcTemplate.update(rateSql, filmId);
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String topFilmSql = "SELECT f.ID f_id, f.NAME f_name, f.DESCRIPTION f_description, f.RELEASEDATE f_date, f.DURATION f_duration, f.RATE f_rate, mr.ID mr_id, mr.NAME mr_name "
                + "FROM  FILMS f "
                + "JOIN MPA_RATING mr ON f.MPA_ID = mr.ID "
                + "ORDER BY f_rate DESC "
                + "LIMIT ?;";

        String genreSgl = "SELECT fg.GENRE_ID, g.NAME FROM FILMS_GENRE fg JOIN GENRE g  ON fg.GENRE_ID = g.ID WHERE fg.FILM_ID = ?";

        List<Film> topFilms = jdbcTemplate.query(topFilmSql, filmRowMapper(), count);
        for (Film film : topFilms) {
            film.getGenres().addAll(jdbcTemplate.query(genreSgl, genreRowMapper(), film.getId()));
        }
        return topFilms;
    }

    private RowMapper<Film> filmRowMapper() {
        return new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Film(rs.getLong("f_id"),
                        rs.getString("f_name"),
                        rs.getString("f_description"),
                        rs.getDate("f_date").toLocalDate(),
                        rs.getLong("f_duration"),
                        new MpaRating(rs.getLong("mr_id"), rs.getString("mr_name")),
                        rs.getLong("f_rate"),
                        new TreeSet<>(Genre::compareTo));
            }
        };
    }

    private RowMapper<Genre> genreRowMapper() {
        return new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Genre(rs.getLong("genre_id"), rs.getString("name"));
            }
        };
    }

}
