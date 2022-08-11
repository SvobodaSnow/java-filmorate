package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User addUser(User user);

    User updateUser(User user);

    List<User> findAllFriends(int id);

    User getUserById(int id);
}
