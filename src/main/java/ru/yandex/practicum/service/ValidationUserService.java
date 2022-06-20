package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
public class ValidationUserService {
    public static void checkEmailIsBlank(User user) {
        if (user.getEmail().isBlank()) {
            log.debug("Получен запрос без электронной почты");
            throw new ValidationException("Не указан адрес электронной почты");
        }
    }

    public static void checkEmailIsCorrect(User user) {
        if (!user.getEmail().contains("@")) {
            log.debug("Получен запрос с неправильной электронной почтой");
            throw new ValidationException("Некоректный адрес почты");
        }
    }

    public static void checkLoginIsBlank(User user) {
        if (user.getLogin().isBlank()) {
            log.debug("Получен запрос без логина");
            throw new ValidationException("Не указан логин");
        }
    }

    public static void checkLoginContainsSpaces(User user) {
        if (user.getLogin().contains(" ")) {
            log.debug("Получен запрос с логином, в котором есть пробелы");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
    }

    public static void checkNameIsCorrect(User user) {
        if (user.getName().isBlank()) {
            log.debug("Получен запрос на добавление пользователя без имени, заменен на логин");
            user.setName(user.getLogin());
        }
    }

    public static void checkBirthdayFuture(User user) {
        LocalDate currentDate = LocalDate.now();
        if (user.getBirthday().isAfter(currentDate)) {
            log.debug("Получен запрос на добаление пользователя с днем рождения в будущем. День рождения "
                    + user.getBirthday());
            throw new ValidationException("День рождения в будущем");
        }
    }

    public static void checkMovieAvailability(Map<Integer, User> users, User user) {
        if (users.get(user.getId()) == null) {
            log.debug("Получен запрос на обновление пользователя с несуществующим ID. ID " + user.getId());
            throw new ValidationException("Пользователя нет в списке");
        }
    }
}
