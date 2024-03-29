package ru.yandex.practicum.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.user.UserService;

import java.util.*;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Получен запрос на получение списка пользователей");
        return userService.findAll();
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) {
        User newUser = userService.addUser(user);
        log.info("Новый пользователь добавлен c ID " + newUser.getId());
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        User newUser = userService.updateUser(user);
        log.info("Пользователь c ID" + newUser.getId() + " обновлен");
        return user;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        log.info("Пользователь с ID " + id + " успешно получен");
        return user;
    }

    @PutMapping("/users/{id}/friends/{friendsId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendsId) {
        User newUser = userService.addFriend(id, friendsId);
        log.info("Пользователю с ID " + id + " добавлен в друзья пользователь с ID " + friendsId);
        return newUser;
    }

    @DeleteMapping("/users/{id}/friends/{friendsId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendsId) {
        User newUser = userService.deleteFriend(id, friendsId);
        log.info("Пользователь с ID " + id + " удалил из друзей пользователя с ID " + friendsId);
        return newUser;
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable int id) {
        List<User> friends = userService.findAllFriends(id);
        log.info("Список друзей пользователя с ID " + id + " успешно сформирован");
        return friends;
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getSharedFriendsList(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен запрос на получения общих друзей пользвателей с ID " + id + " и " + otherId);
        User user = userService.getUserById(id);
        User otherUser = userService.getUserById(otherId);
        if (user.getFriends() == null || otherUser.getFriends() == null) {
            return new ArrayList<>();
        }
        List<Integer> userFriendsId = new ArrayList<>(user.getFriends());
        List<Integer> otherFriendsId = new ArrayList<>(otherUser.getFriends());
        userFriendsId.retainAll(otherFriendsId);
        List<User> userFriends = new ArrayList<>();
        for (Integer userFriendId : userFriendsId) {
            userFriends.add(userService.getUserById(userFriendId));
        }
        return userFriends;
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        userService.deleteUserById(userId);
        log.info("Пользователь с ID " + userId + " успешно удален");
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendationsFilmsByIdUser(@PathVariable int id) {
        List<Film> films = userService.getRecommendationsFilmsByIdUser(id);
        log.info("Список рекомендованных фильмов для пользователя с ID " + id + " успешно сформирован");
        return films;
    }
}
