package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
public class UserValidationService {
    public void checkEmailIsBlank(User user) {
        if (user.getEmail().isBlank()) {
            log.error("Получен запрос без электронной почты");
            throw new ValidationException("Не указан адрес электронной почты");
        }
    }

    public void checkEmailIsCorrect(User user) {
        if (!user.getEmail().contains("@")) {
            log.error("Получен запрос с неправильной электронной почтой");
            throw new ValidationException("Некоректный адрес почты");
        }
    }

    public void checkLoginIsBlank(User user) {
        if (user.getLogin().isBlank()) {
            log.error("Получен запрос без логина");
            throw new ValidationException("Не указан логин");
        }
    }

    public void checkLoginContainsSpaces(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Получен запрос с логином, в котором есть пробелы");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
    }

    public void checkNameIsCorrect(User user) {
        if (user.getName().isBlank()) {
            log.error("Получен запрос на добавление пользователя без имени, заменен на логин");
            user.setName(user.getLogin());
        }
    }

    public void checkBirthdayFuture(User user) {
        LocalDate currentDate = LocalDate.now();
        if (user.getBirthday().isAfter(currentDate)) {
            log.error("Получен запрос на добаление пользователя с днем рождения в будущем. День рождения "
                    + user.getBirthday());
            throw new ValidationException("День рождения в будущем");
        }
    }

    public void checkUserAvailability(Map<Integer, User> users, User user) {
        if (users.get(user.getId()) == null) {
            log.error("Получен запрос на обновление пользователя с несуществующим ID. ID " + user.getId());
            throw new NotFoundException("Пользователя нет в списке");
        }
    }

    public void checkUserAvailability(Map<Integer, User> users, int userId) {
        if (users.get(userId) == null) {
            log.error("Получен запрос на получение пользователя с несуществующим ID. ID " + userId);
            throw new NotFoundException("Пользователя нет в списке");
        }
    }
}
