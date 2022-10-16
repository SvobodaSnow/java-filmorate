package ru.yandex.practicum.storage.user;

import ru.yandex.practicum.model.user.User;

public interface UnconfirmedRequestStorage {
    void addUnconfirmedRequest(int userId, int friendId);

    void deleteUnconfirmedRequest(int userId, int friendId);

    boolean checkRequestFriend(int userId, int friendId);

    void fillReceivedFriendshipRequests(User user);

    void fillSentFriendshipRequests(User user);

    void deleteAllRequestsFriends(int userId);
}
