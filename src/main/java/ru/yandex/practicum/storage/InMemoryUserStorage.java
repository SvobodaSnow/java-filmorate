package ru.yandex.practicum.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.IdGenerator;
import ru.yandex.practicum.service.UserValidationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    @Autowired
    private UserValidationService userValidationService;
    @Autowired
    private IdGenerator idGenerator;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        userValidationService.checkEmailIsBlank(user);
        userValidationService.checkEmailIsCorrect(user);
        userValidationService.checkLoginIsBlank(user);
        userValidationService.checkLoginContainsSpaces(user);
        userValidationService.checkNameIsCorrect(user);
        userValidationService.checkBirthdayFuture(user);
        user.setId(idGenerator.generateIdUser());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        userValidationService.checkUserAvailability(users, user);
        userValidationService.checkEmailIsBlank(user);
        userValidationService.checkEmailIsCorrect(user);
        userValidationService.checkLoginIsBlank(user);
        userValidationService.checkLoginContainsSpaces(user);
        userValidationService.checkNameIsCorrect(user);
        userValidationService.checkBirthdayFuture(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAllFriends(int id) {
        userValidationService.checkUserAvailability(users, id);
        User user = users.get(id);
        List<User> friends = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            friends.add(users.get(friendId));
        }
        return friends;
    }

    @Override
    public User getUserById(int id) {
        userValidationService.checkUserAvailability(users, id);
        return users.get(id);
    }

    @Override
    public User addUserFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.addFriend(friend);
        friend.addFriend(user);
        return user;
    }

    @Override
    public User deleteUserFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.deleteFriend(friend);
        friend.deleteFriend(user);
        return user;
    }

    @Override
    public User sendFriendRequest(int userId, int friendId) {
        userValidationService.checkUserAvailability(users, userId);
        userValidationService.checkUserAvailability(users, friendId);
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.addSentFriendshipRequests(friend);
        friend.addReceivedFriendshipRequests(user);
        return user;
    }

    @Override
    public User deleteFriendRequest(int userId, int friendId) {
        userValidationService.checkUserAvailability(users, userId);
        userValidationService.checkUserAvailability(users, friendId);
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.deleteSentFriendshipRequests(friend);
        friend.deleteReceivedFriendshipRequests(friend);
        return user;
    }

    @Override
    public User confirmFriendRequest(int userId, int friendId) {
        userValidationService.checkUserAvailability(users, userId);
        userValidationService.checkUserAvailability(users, friendId);
        addUserFriend(userId, friendId);
        deleteFriendRequest(friendId, userId);
        return users.get(userId);
    }

    @Override
    public boolean checkRequestFriend(int userId, int friendId) {
        userValidationService.checkUserAvailability(users, userId);
        userValidationService.checkUserAvailability(users, friendId);
        return users.get(userId).getReceivedFriendshipRequests().contains(friendId);
    }

    @Override
    public void deleteUserById(int userId) {
        users.remove(userId);
    }

    @Override
    public void deleteAllFriendsUserById(int userId) {
        users.get(userId).getFriends().clear();
    }

    @Override
    public void deleteAllRequestsFriends(int userId) {
        users.get(userId).getReceivedFriendshipRequests().clear();
        users.get(userId).getSentFriendshipRequests().clear();
    }
}
