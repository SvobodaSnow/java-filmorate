package ru.yandex.practicum.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserValidationDBService;
import ru.yandex.practicum.service.UserValidationService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Primary
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private UserValidationService userValidationService;
    @Autowired
    private UserValidationDBService userValidationDBService;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        for (User user : users) {
            fillFriendsListForUser(user);
            fillSentFriendshipRequests(user);
            fillReceivedFriendshipRequests(user);
        }
        return users;
    }

    @Override
    public User addUser(User user) {
        checkUserValidation(user);
        userValidationDBService.checkEmailInDatabase(user);
        String sqlInsertUser = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertUser, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        String sqlId = "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlId);
        if (userRows.next()) {
            user.setId(userRows.getInt("user_id"));
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserValidation(user);
        userValidationDBService.checkIdDatabase(user.getId());
        String sqlUpdateUser = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(
                sqlUpdateUser,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public List<User> findAllFriends(int id) {
        userValidationDBService.checkIdDatabase(id);
        List<Integer> friendsIdList = fillFriendsList(id);
        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : friendsIdList) {
            friendsList.add(getUserById(friendId));
        }
        return friendsList;
    }

    @Override
    public User getUserById(int id) {
        userValidationDBService.checkIdDatabase(id);
        String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        User user = new User();
        if (userRows.next()) {
            user.setId(id);
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
        }
        fillFriendsListForUser(user);
        fillSentFriendshipRequests(user);
        fillReceivedFriendshipRequests(user);
        return user;
    }

    @Override
    public User addUserFriend(int userId, int friendId) {
        userValidationDBService.checkIdDatabase(userId);
        userValidationDBService.checkIdDatabase(friendId);
        String sqlAddFriend = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddFriend, userId, friendId);
        return getUserById(userId);
    }

    @Override
    public User deleteUserFriend(int userId, int friendId) {
        userValidationDBService.checkIdDatabase(userId);
        userValidationDBService.checkIdDatabase(friendId);
        String sqlDeleteFriend = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDeleteFriend, userId, friendId);
        return getUserById(userId);
    }

    @Override
    public User sendFriendRequest(int userId, int friendId) {
        userValidationDBService.checkIdDatabase(userId);
        userValidationDBService.checkIdDatabase(friendId);
        String sqlAddRequestFriend = "INSERT INTO unconfirmed_requests (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddRequestFriend, userId, friendId);
        String sqlAddFriend = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddFriend, userId, friendId);
        return getUserById(userId);
    }

    @Override
    public User deleteFriendRequest(int userId, int friendId) {
        userValidationDBService.checkIdDatabase(userId);
        userValidationDBService.checkIdDatabase(friendId);
        String sqlDeleteFriend = "DELETE FROM unconfirmed_requests WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDeleteFriend, userId, friendId);
        sqlDeleteFriend = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDeleteFriend, userId, friendId);
        return getUserById(userId);
    }

    @Override
    public User confirmFriendRequest(int userId, int friendId) {
        userValidationDBService.checkIdDatabase(userId);
        userValidationDBService.checkIdDatabase(friendId);
        addUserFriend(userId, friendId);
        deleteFriendRequest(friendId, userId);
        return getUserById(userId);
    }

    @Override
    public boolean checkRequestFriend(int userId, int friendId) {
        userValidationDBService.checkIdDatabase(userId);
        userValidationDBService.checkIdDatabase(friendId);
        String sql = "SELECT * FROM unconfirmed_requests WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        return userRows.next();
    }

    private int getFriendsId(ResultSet rs) throws SQLException {
        return rs.getInt("friend_id");
    }

    private int getReceivedFriendshipRequests(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    private void fillFriendsListForUser(User user) {
        user.setFriends(new HashSet<>(fillFriendsList(user.getId())));
    }

    private void fillSentFriendshipRequests(User user) {
        String sql = "SELECT friend_id FROM unconfirmed_requests WHERE user_id = ?";
        List<Integer> sentFriendshipRequests = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getFriendsId(rs),
                user.getId()
        );
        user.setSentFriendshipRequests(new HashSet<>(sentFriendshipRequests));
    }

    private void fillReceivedFriendshipRequests(User user) {
        String sql = "SELECT user_id FROM unconfirmed_requests WHERE friend_id = ?";
        List<Integer> receivedFriendshipRequests = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getReceivedFriendshipRequests(rs),
                user.getId()
        );
        user.setReceivedFriendshipRequests(new HashSet<>(receivedFriendshipRequests));
    }

    private List<Integer> fillFriendsList(int id) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getFriendsId(rs),
                id
        );
    }

    private void checkUserValidation(User user) {
        userValidationService.checkEmailIsBlank(user);
        userValidationService.checkEmailIsCorrect(user);
        userValidationService.checkLoginIsBlank(user);
        userValidationService.checkLoginContainsSpaces(user);
        userValidationService.checkNameIsCorrect(user);
        userValidationService.checkBirthdayFuture(user);
    }
}
