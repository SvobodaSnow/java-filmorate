package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.IdGenerator;
import ru.yandex.practicum.service.UserValidationService;

import java.util.*;

@Slf4j
@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();
    private final UserValidationService userValidationService = new UserValidationService();

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Получен запрос на получение списка пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) {
        log.info("Получен запрос на добавление нового пользователя");
        userValidationService.checkEmailIsBlank(user);
        userValidationService.checkEmailIsCorrect(user);
        userValidationService.checkLoginIsBlank(user);
        userValidationService.checkLoginContainsSpaces(user);
        userValidationService.checkNameIsCorrect(user);
        userValidationService.checkBirthdayFuture(user);
        user.setId(idGenerator.generate());
        users.put(user.getId(), user);
        log.info("Новый пользователь добавлен c ID " + user.getId());
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        log.info("Получен запрос на обновление пользователя");
        userValidationService.checkUserAvailability(users, user);
        userValidationService.checkEmailIsBlank(user);
        userValidationService.checkEmailIsCorrect(user);
        userValidationService.checkLoginIsBlank(user);
        userValidationService.checkLoginContainsSpaces(user);
        userValidationService.checkNameIsCorrect(user);
        userValidationService.checkBirthdayFuture(user);
        users.put(user.getId(), user);
        log.info("Пользователь c ID" + user.getId() + " обновлен");
        return user;
    }
}
