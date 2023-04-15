package ru.yandex.practicum.filmorate.storage.InDbImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MetaDataStorage;

import java.util.List;

@Slf4j
@Component
public class MetaDataDbStorage implements MetaDataStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MetaDataDbStorage(JdbcTemplate jdbcTemplate) {
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

    public List<MpaRating> getAllMpaRating() {
        return jdbcTemplate.query("SELECT * FROM mpa_rating", mpaRatingRowMapper());
    }

    public MpaRating getMpaRatingById(Long id) {
        MpaRating mpaRating = null;
        try {
            mpaRating = jdbcTemplate.queryForObject("SELECT * FROM mpa_rating WHERE id = ?", mpaRatingRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("Жанр с Id = %s не найден", id);
            log.error(message);
            throw new DataNotFoundException(message);
        }
        return mpaRating;
    }


    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name"));
    }

    private RowMapper<MpaRating> mpaRatingRowMapper() {
        return (rs, rowNum) -> new MpaRating(rs.getLong("id"), rs.getString("name"));
    }
}
