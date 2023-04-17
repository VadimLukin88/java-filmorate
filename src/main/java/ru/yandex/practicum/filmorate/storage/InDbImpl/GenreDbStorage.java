package ru.yandex.practicum.filmorate.storage.InDbImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genre", genreRowMapper());
    }

    public Genre getGenreById(Long id) {
        Genre genre = null;
        try {
            genre = jdbcTemplate.queryForObject("SELECT * FROM genre WHERE id = ?", genreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("Жанр с Id = %s не найден", id);
            log.error(message);
            throw new DataNotFoundException(message);
        }
        return genre;
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name"));
    }

}
