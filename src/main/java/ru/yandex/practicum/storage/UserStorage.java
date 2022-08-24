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

    User addUserFriend(int userId, int friendId);

    User deleteUserFriend(int userId, int friendId);

    User sendFriendRequest(int userId, int friendId);

    User deleteFriendRequest(int userId, int friendId);

    User confirmFriendRequest(int userId, int friendId);

    boolean checkRequestFriend(int userId, int friendId);
}
