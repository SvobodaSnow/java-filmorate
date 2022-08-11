package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends;

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
}
