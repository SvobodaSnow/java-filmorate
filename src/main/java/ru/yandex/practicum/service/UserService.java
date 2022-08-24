package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        User user;
        if (userStorage.checkRequestFriend(friendId, userId)) {
            user = userStorage.confirmFriendRequest(userId, friendId);
        } else {
            user = userStorage.sendFriendRequest(userId, friendId);
        }
        return userStorage.updateUser(user);
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.deleteUserFriend(userId, friendId);
        User friend = userStorage.deleteUserFriend(friendId, userId);
        return userStorage.updateUser(user);
    }

    public List<User> findAllFriends(int id) {
        return userStorage.findAllFriends(id);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }
}
