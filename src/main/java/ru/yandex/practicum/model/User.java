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
    private Set<Integer> friends = new HashSet<>();
    private Set<Integer> sentFriendshipRequests = new HashSet<>();
    private Set<Integer> receivedFriendshipRequests = new HashSet<>();

    public void addFriend(User newFriend) {
        friends.add(newFriend.getId());
    }

    public void deleteFriend(User deletedFriend) {
        friends.remove(deletedFriend.getId());
    }

    public void addSentFriendshipRequests(User newFriend) {
        sentFriendshipRequests.add(newFriend.getId());
    }

    public void addReceivedFriendshipRequests(User newFriend) {
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
