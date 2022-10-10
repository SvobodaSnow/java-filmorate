package ru.yandex.practicum.storage.user;

import ru.yandex.practicum.model.user.User;

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

    void deleteUserById(int userId);

    void deleteAllFriendsUserById(int userId);

    void deleteAllRequestsFriends(int userId);
}
