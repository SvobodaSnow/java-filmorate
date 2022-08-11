package ru.yandex.practicum.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.IdGenerator;
import ru.yandex.practicum.service.UserValidationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();
    private final UserValidationService userValidationService = new UserValidationService();

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
        user.setId(idGenerator.generate());
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
}
