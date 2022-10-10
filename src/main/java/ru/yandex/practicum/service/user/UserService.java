package ru.yandex.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.storage.user.UserStorage;
import ru.yandex.practicum.storage.feed.FeedStorage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;


    public User addFriend(int userId, int friendId) {
        User user;
        if (userStorage.checkRequestFriend(friendId, userId)) {
            user = userStorage.confirmFriendRequest(userId, friendId);
        } else {
            user = userStorage.sendFriendRequest(userId, friendId);
        }
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                userId, 3, 5, friendId);
        return userStorage.updateUser(user);
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.deleteUserFriend(userId, friendId);
        userStorage.deleteUserFriend(friendId, userId);
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(), userId,
                3, 4, friendId);
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

    public void deleteUserById(int userId) {
        userStorage.deleteAllFriendsUserById(userId);
        userStorage.deleteAllRequestsFriends(userId);
        userStorage.deleteUserById(userId);
    }
}
