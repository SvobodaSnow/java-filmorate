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
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friend);
        friend.addFriend(user);
        userStorage.updateUser(friend);
        return userStorage.updateUser(user);
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.deleteFriend(friend);
        friend.addFriend(user);
        userStorage.updateUser(friend);
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
