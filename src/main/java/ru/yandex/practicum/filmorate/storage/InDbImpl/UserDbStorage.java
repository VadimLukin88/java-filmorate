package ru.yandex.practicum.filmorate.storage.InDbImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users;";

        return jdbcTemplate.query(sql, userRowMapper());
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {   // требование из ТЗ_9 - если NAME не заполнено, то записываем в NAME значение из LOGIN
            user.setName(user.getLogin());
        }
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES(?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();

        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?;";
        int result = jdbcTemplate.update(sql,
                        user.getEmail(),
                        user.getLogin(),
                        user.getName(),
                        user.getBirthday(),
                        user.getId());
        if (result == 0) {
            throw new DataNotFoundException(String.format("Пользователь с Id=%s не найден", user.getId()));
        }
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?;";
        int result = jdbcTemplate.update(sql,id);
        if (result == 0) {
            throw new DataNotFoundException(String.format("Пользователь с Id=%s не найден", id));
        }
    }

    @Override
    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?;";

        return jdbcTemplate.queryForObject(sql, userRowMapper(), id);
    }

    @Override
    public void validateId(Long id) {
        String sql = "SELECT TOP 1 1 FROM users u WHERE u.id = ?;";

        try {
            jdbcTemplate.queryForObject(sql, Long.class, id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("Пользователь с Id = %s не найден", id);

            log.error(message);
            throw new DataNotFoundException(message);
        }
    }

    @Override
    public void addToFriends(Long userId, Long friendId) {
        String sql = "INSERT INTO friendship VALUES(?,?);";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFromFriends(Long userId, Long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getAllUsersFriends(Long userId) {
        String sql = new StringBuilder()
                .append("SELECT * FROM users us WHERE us.id IN ")
                .append("( SELECT friend_id FROM friendship fs WHERE fs.user_id = ? );")
                .toString();
        return jdbcTemplate.query(sql, userRowMapper(), userId);
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long otherId) {

        String sql = new StringBuilder()
                            .append("SELECT * FROM users us WHERE us.id IN ")
                            .append("( SELECT f1.friend_id FROM friendship f1 WHERE f1.user_id = ? ")
                            .append("INTERSECT ")
                            .append("SELECT f2.friend_id FROM friendship f2 WHERE f2.user_id = ? );")
                            .toString();
        return jdbcTemplate.query(sql, userRowMapper(), userId, otherId);
    }

    private RowMapper<User> userRowMapper() {
        return new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new User(rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate());
            }
        };
    }
}
