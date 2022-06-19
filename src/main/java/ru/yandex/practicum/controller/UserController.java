package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exceptions.InvalidBirthdayException;
import ru.yandex.practicum.exceptions.InvalidEmailException;
import ru.yandex.practicum.exceptions.InvalidLoginException;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
public class UserController {
    private final List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Получен запрос на получение списка пользователей");
        return users;
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) throws
            InvalidEmailException, InvalidLoginException, InvalidBirthdayException {
        log.info("Получен запрос на добавление нового пользователя");
        if (user.getEmail().isBlank()) {
            log.debug("Получен запрос без электронной почты");
            throw new InvalidEmailException("Не указан адрес электронной почты");
        }
        if (!user.getEmail().contains("@")) {
            log.debug("Получен запрос с неправильной электронной почтой");
            throw new InvalidEmailException("Некоректный адрес почты");
        }
        if (user.getLogin().isBlank()) {
            log.debug("Получен запрос без логина");
            throw new InvalidLoginException("Не указан логин");
        }
        if (user.getLogin().contains(" ")) {
            log.debug("Получен запрос с логином, в котором есть пробелы");
            throw new InvalidLoginException("Логин не должен содержать пробелы");
        }
        if (user.getName().isBlank()) {
            log.debug("Получен запрос на добавление пользователя без имени, заменен на логин");
            user.setName(user.getLogin());
        }
        LocalDate currentDate = LocalDate.now();
        if (user.getBirthday().isAfter(currentDate)) {
            log.debug("Получен запрос на добаление пользователя с днем рождения в будующем");
            throw new InvalidBirthdayException("День рождения в будующем");
        }
        user.setId(users.size() + 1);
        users.add(user);
        log.info("Новый пользователь добавлен");
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) throws
            InvalidEmailException, InvalidLoginException, InvalidBirthdayException {
        log.info("Получен запрос на обновление пользователя");
        if (user.getEmail().isBlank()) {
            log.debug("Получен запрос без электронной почты");
            throw new InvalidEmailException("Не указан адрес электронной почты");
        }
        if (!user.getEmail().contains("@")) {
            log.debug("Получен запрос с неправильной электронной почтой");
            throw new InvalidEmailException("Некоректный адрес почты");
        }
        if (user.getLogin().isBlank()) {
            log.debug("Получен запрос без логина");
            throw new InvalidLoginException("Не указан логин");
        }
        if (user.getLogin().contains(" ")) {
            log.debug("Получен запрос с логином, в котором есть пробелы");
            throw new InvalidLoginException("Логин не должен содержать пробелы");
        }
        if (user.getName().isBlank()) {
            log.debug("Получен запрос на добавление пользователя без имени, заменен на логин");
            user.setName(user.getLogin());
        }
        LocalDate currentDate = LocalDate.now();
        if (user.getBirthday().isAfter(currentDate)) {
            log.debug("Получен запрос на добаление пользователя с днем рождения в будующем");
            throw new InvalidBirthdayException("День рождения в будующем");
        }
        users.set(user.getId() - 1, user);
        log.info("Пользователь обновлен");
        return user;
    }
}
