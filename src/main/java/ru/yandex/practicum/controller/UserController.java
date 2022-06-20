package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.IdGenerator;

import java.util.*;

import static ru.yandex.practicum.service.ValidationUserService.*;

@Slf4j
@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Получен запрос на получение списка пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) {
        log.info("Получен запрос на добавление нового пользователя");
        checkEmailIsBlank(user);
        checkEmailIsCorrect(user);
        checkLoginIsBlank(user);
        checkLoginContainsSpaces(user);
        checkNameIsCorrect(user);
        checkBirthdayFuture(user);
        user.setId(idGenerator.generate());
        users.put(user.getId(), user);
        log.info("Новый пользователь добавлен c ID " + user.getId());
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        log.info("Получен запрос на обновление пользователя");
        checkEmailIsBlank(user);
        checkEmailIsCorrect(user);
        checkLoginIsBlank(user);
        checkLoginContainsSpaces(user);
        checkNameIsCorrect(user);
        checkBirthdayFuture(user);
        users.put(user.getId(), user);
        log.info("Пользователь c ID" + user.getId() + " обновлен");
        return user;
    }
}
