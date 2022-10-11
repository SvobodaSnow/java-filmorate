package ru.yandex.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.film.FilmService;
import ru.yandex.practicum.storage.film.FilmStorage;
import ru.yandex.practicum.storage.likes.LikesStorage;
import ru.yandex.practicum.storage.user.FriendsStorage;
import ru.yandex.practicum.storage.user.UnconfirmedRequestStorage;
import ru.yandex.practicum.storage.user.UserStorage;
import ru.yandex.practicum.storage.feed.FeedStorage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;
    private final FriendsStorage friendsStorage;
    private final UnconfirmedRequestStorage unconfirmedRequestStorage;
    private final LikesStorage likesStorage;
    private final FilmService filmService;

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

    public List<Film> getRecommendationsFilmsByIdUser(int userId) {
        User desiredUser = getUserById(userId);

        List<User> users = findAll();
        Map<User, List<Integer>> likesUsers = new HashMap<>();
        for (User user : users) {
            likesUsers.put(user, likesStorage.fillFilmLikeListForFilm(user.getId()));
        }

        List<Integer> desiredUserLikes = likesUsers.get(desiredUser);
        likesUsers.remove(desiredUser);

        Map<User, Integer> crossingLikes = new HashMap<>();
        int count;
        int maxCount = 0;
        for (Map.Entry<User, List<Integer>> likesUser : likesUsers.entrySet()) {
            count = 0;
            for (Integer likeDesiredUser : desiredUserLikes) {
                if (likesUser.getValue().contains(likeDesiredUser)) {
                    count ++;
                }
            }
            if (maxCount == count) {
                crossingLikes.put(likesUser.getKey(), count);
            }
            if (maxCount < count) {
                maxCount = count;
                crossingLikes.clear();
                crossingLikes.put(likesUser.getKey(), count);
            }
        }

        Set<Integer> filmsId = new HashSet<>();
        for (User user : crossingLikes.keySet()) {
            List<Integer> addFilmId = likesUsers.get(user);
            addFilmId.removeAll(desiredUserLikes);
            filmsId.addAll(addFilmId);
        }

        List<Film> films = new ArrayList<>();
        for (Integer filmId : filmsId) {
            films.add(filmService.getFilmById(filmId));
        }

        return films;
    }
}
