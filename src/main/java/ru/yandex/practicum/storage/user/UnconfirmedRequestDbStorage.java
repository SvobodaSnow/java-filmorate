package ru.yandex.practicum.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Primary
@Component
public class UnconfirmedRequestDbStorage implements UnconfirmedRequestStorage {
    private final JdbcTemplate jdbcTemplate;

    public UnconfirmedRequestDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addUnconfirmedRequest(int userId, int friendId) {
        String sqlAddRequestFriend = "INSERT INTO unconfirmed_requests (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddRequestFriend, userId, friendId);
    }

    @Override
    public void deleteUnconfirmedRequest(int userId, int friendId) {
        String sqlDeleteFriend = "DELETE FROM unconfirmed_requests WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDeleteFriend, userId, friendId);
    }

    @Override
    public boolean checkRequestFriend(int userId, int friendId) {
        String sql = "SELECT * FROM unconfirmed_requests WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        return userRows.next();
    }

    @Override
    public void fillReceivedFriendshipRequests(User user) {
        String sql = "SELECT user_id FROM unconfirmed_requests WHERE friend_id = ?";
        List<Integer> receivedFriendshipRequests = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getReceivedFriendshipRequests(rs),
                user.getId()
        );
        user.setReceivedFriendshipRequests(new HashSet<>(receivedFriendshipRequests));
    }

    @Override
    public void fillSentFriendshipRequests(User user) {
        String sql = "SELECT friend_id FROM unconfirmed_requests WHERE user_id = ?";
        List<Integer> sentFriendshipRequests = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getFriendsId(rs),
                user.getId()
        );
        user.setSentFriendshipRequests(new HashSet<>(sentFriendshipRequests));
    }

    @Override
    public void deleteAllRequestsFriends(int userId) {
        String sql = "DELETE FROM unconfirmed_requests WHERE user_id = ? OR friend_id = ?";
        jdbcTemplate.update(sql, userId, userId);
    }

    private int getReceivedFriendshipRequests(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }

    private int getFriendsId(ResultSet rs) throws SQLException {
        return rs.getInt("friend_id");
    }
}
