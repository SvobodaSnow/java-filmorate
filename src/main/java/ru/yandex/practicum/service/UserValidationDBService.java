package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.User;

@Slf4j
@Service
public class UserValidationDBService {
    private final JdbcTemplate jdbcTemplate;

    public UserValidationDBService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkIdDatabase(int id) {
        String sql = "SELECT email FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if(!userRows.next()) {
            log.error("Получен запрос на обновление пользовактеля с ID " + id + ", которого нет в базе данных");
            throw new NotFoundException("Пользователя нет в списке");
        }
    }

    public void checkEmailInDatabase(User user) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, user.getEmail());
        if(userRows.next()) {
            log.error("Получен запрос на добавление пользователя, email которого уже есть в базе данных. EMAIL "
                    + user.getEmail());
            throw new ValidationException("Адрес почты есть в базе данных");
        }
    }
}
