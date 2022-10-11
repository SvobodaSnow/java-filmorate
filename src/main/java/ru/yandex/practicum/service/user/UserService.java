package ru.yandex.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.storage.user.FriendsStorage;
import ru.yandex.practicum.storage.user.UnconfirmedRequestStorage;
import ru.yandex.practicum.storage.user.UserStorage;
import ru.yandex.practicum.storage.feed.FeedStorage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;
    private final FriendsStorage friendsStorage;
    private final UnconfirmedRequestStorage unconfirmedRequestStorage;

    public User addFriend(int userId, int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        if (unconfirmedRequestStorage.checkRequestFriend(friendId, userId)) {
            unconfirmedRequestStorage.deleteUnconfirmedRequest(friendId, userId);
        } else {
            unconfirmedRequestStorage.addUnconfirmedRequest(userId, friendId);
        }
        friendsStorage.addUserFriend(userId, friendId);
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                userId, 3, 5, friendId);
        return userStorage.getUserById(userId);
    }

    public User deleteFriend(int userId, int friendId) {
        friendsStorage.deleteFriendRequest(userId, friendId);
        friendsStorage.deleteFriendRequest(friendId, userId);
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(), userId,
                3, 4, friendId);
        return userStorage.getUserById(userId);
    }

    public List<User> findAllFriends(int id) {
        User user = getUserById(id);
        List<Integer> friendsIdList = friendsStorage.fillFriendsList(id);
        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : friendsIdList) {
            friendsList.add(getUserById(friendId));
        }
        return friendsList;
    }

    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        for (User user : users) {
            friendsStorage.fillFriendsListForUser(user);
            unconfirmedRequestStorage.fillSentFriendshipRequests(user);
            unconfirmedRequestStorage.fillReceivedFriendshipRequests(user);
        }
        return users;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        User user = userStorage.getUserById(id);
        friendsStorage.fillFriendsListForUser(user);
        unconfirmedRequestStorage.fillSentFriendshipRequests(user);
        unconfirmedRequestStorage.fillReceivedFriendshipRequests(user);
        return user;
    }

    public void deleteUserById(int userId) {
        friendsStorage.deleteAllFriendsUserById(userId);
        unconfirmedRequestStorage.deleteAllRequestsFriends(userId);
        userStorage.deleteUserById(userId);
    }
}
