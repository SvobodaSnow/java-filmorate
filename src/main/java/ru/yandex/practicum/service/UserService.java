package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.Storages;
import ru.yandex.practicum.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage = Storages.getDefaultInMemoryUserStorage();

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
}
