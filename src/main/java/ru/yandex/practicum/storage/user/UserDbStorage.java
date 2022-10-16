package ru.yandex.practicum.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.user.UserValidationService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Primary
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private UserValidationService userValidationService;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User addUser(User user) {
        checkUserValidation(user);
        String sqlInsertUser = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertUser, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        String sqlId = "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlId);
        if (userRows.next()) {
            user.setId(userRows.getInt("user_id"));
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserValidation(user);
        String sqlUpdateUser = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(
                sqlUpdateUser,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return getUserById(user.getId());
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        User user = new User();
        if (userRows.next()) {
            user.setId(id);
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
        } else {
            log.error("Получен запрос на обновление пользовактеля с ID " + id + ", которого нет в базе данных");
            throw new NotFoundException("Пользователя нет в списке");
        }
        return user;
    }

    @Override
    public void deleteUserById(int userId) {
        getUserById(userId);
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    private void checkUserValidation(User user) {
        userValidationService.checkEmailIsBlank(user);
        userValidationService.checkEmailIsCorrect(user);
        userValidationService.checkLoginIsBlank(user);
        userValidationService.checkLoginContainsSpaces(user);
        userValidationService.checkNameIsCorrect(user);
        userValidationService.checkBirthdayFuture(user);
    }
}
