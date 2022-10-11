package ru.yandex.practicum.storage.user;

import ru.yandex.practicum.model.user.User;

import java.util.List;

public interface FriendsStorage {

    void addUserFriend(int userId, int friendId);

    void deleteFriendRequest(int userId, int friendId);

    void fillFriendsListForUser(User user);

    List<Integer> fillFriendsList(int id);

    void deleteAllFriendsUserById(int userId);
}
