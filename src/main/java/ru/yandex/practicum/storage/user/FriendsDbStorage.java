package ru.yandex.practicum.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Primary
@Component
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addUserFriend(int userId, int friendId) {
        String sqlAddFriend = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddFriend, userId, friendId);
    }

    @Override
    public void deleteFriendRequest(int userId, int friendId) {
        String sqlDeleteFriend = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDeleteFriend, userId, friendId);
    }

    @Override
    public void fillFriendsListForUser(User user) {
        user.setFriends(new HashSet<>(fillFriendsList(user.getId())));
    }

    @Override
    public List<Integer> fillFriendsList(int id) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getFriendsId(rs),
                id
        );
    }

    @Override
    public void deleteAllFriendsUserById(int userId) {
        String sql = "DELETE FROM friends WHERE user_id = ? OR friend_id = ?";
        jdbcTemplate.update(sql, userId, userId);
    }

    private int getFriendsId(ResultSet rs) throws SQLException {
        return rs.getInt("friend_id");
    }
}
