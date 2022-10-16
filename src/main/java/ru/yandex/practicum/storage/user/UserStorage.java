package ru.yandex.practicum.storage.user;

import ru.yandex.practicum.model.user.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User addUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    void deleteUserById(int userId);
}
