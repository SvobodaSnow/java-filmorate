package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends;
    private Set<Integer> sentFriendshipRequests;
    private Set<Integer> receivedFriendshipRequests;

    public void addFriend(User newFriend) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(newFriend.getId());
    }

    public void deleteFriend(User deletedFriend) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.remove(deletedFriend.getId());
    }

    public void addSentFriendshipRequests(User newFriend) {
        if (sentFriendshipRequests == null) {
            sentFriendshipRequests = new HashSet<>();
        }
        sentFriendshipRequests.add(newFriend.getId());
    }

    public void addReceivedFriendshipRequests(User newFriend) {
        if (receivedFriendshipRequests == null) {
            receivedFriendshipRequests = new HashSet<>();
        }
        receivedFriendshipRequests.add(newFriend.getId());
    }

    public void deleteSentFriendshipRequests(User newFriend) {
        sentFriendshipRequests.remove(newFriend.getId());
    }

    public void deleteReceivedFriendshipRequests(User newFriend) {
        receivedFriendshipRequests.remove(newFriend.getId());
    }

    public void confirmSentFriendshipRequests(User friend) {
        if (!sentFriendshipRequests.contains(friend.getId())) {
            return;
        }
        sentFriendshipRequests.remove(friend.getId());
        friends.add(friend.getId());
    }

    public void confirmReceivedFriendshipRequests(User friend) {
        if (!receivedFriendshipRequests.contains(friend.getId())) {
            return;
        }
        receivedFriendshipRequests.remove(friend.getId());
        friends.add(friend.getId());
    }
}
