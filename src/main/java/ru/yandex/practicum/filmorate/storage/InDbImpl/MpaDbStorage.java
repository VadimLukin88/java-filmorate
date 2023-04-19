package ru.yandex.practicum.filmorate.storage.InDbImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    private RowMapper<MpaRating> mpaRatingRowMapper() {
        return (rs, rowNum) -> new MpaRating(rs.getLong("id"), rs.getString("name"));
    }

}
