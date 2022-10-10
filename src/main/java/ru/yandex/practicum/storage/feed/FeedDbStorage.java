package ru.yandex.practicum.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.feed.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getFeedsByUserId(long userId) {
        String sql = "SELECT F.FEED_EVENT_ID, " +
                "F.FEED_TIMESTAMP, " +
                "F.FEED_USER_ID, " +
                "G1.GUIDE_NAME EVENT_TYPE, " +
                "G2.GUIDE_NAME OPERATION, " +
                "F.FEED_ENTITY_ID " +
                "FROM FEED F " +
                "JOIN GUIDE G1 on F.FEED_EVENT_TYPE_ID = G1.GUIDE_DICT_KEY " +
                "JOIN GUIDE G2 ON F.FEED_OPERATION_ID = G2.GUIDE_DICT_KEY " +
                "WHERE FEED_USER_ID = ?";

        return jdbcTemplate.query(sql, FeedDbStorage::makeFeed, userId);
    }

    private static Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return new Feed(rs.getLong("FEED_EVENT_ID"),
                rs.getLong("FEED_TIMESTAMP"),
                rs.getLong("FEED_USER_ID"),
                rs.getString("EVENT_TYPE"),
                rs.getString("OPERATION"),
                rs.getLong("FEED_ENTITY_ID"));
    }

    @Override
    public void addFeed(long timestamp, long userId, Integer eventTypeId, Integer operationId, long entityId) {

        String sql = "INSERT INTO FEED(feed_timestamp, feed_user_id," +
                " feed_event_type_id, feed_operation_id, feed_entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                timestamp,
                userId,
                eventTypeId,
                operationId,
                entityId);
    }

    @Override
    public Optional<Feed> getFeedByReviewId(long reviewId) {
        String sql = "SELECT F.FEED_EVENT_ID, " +
                "F.FEED_TIMESTAMP, " +
                "F.FEED_USER_ID, " +
                "G1.GUIDE_NAME EVENT_TYPE, " +
                "G2.GUIDE_NAME OPERATION, " +
                "F.FEED_ENTITY_ID " +
                "FROM FEED F " +
                "JOIN GUIDE G1 on F.FEED_EVENT_TYPE_ID = G1.GUIDE_DICT_KEY " +
                "JOIN GUIDE G2 ON F.FEED_OPERATION_ID = G2.GUIDE_DICT_KEY " +
                "WHERE G1.GUIDE_NAME = 'REVIEW' AND G2.GUIDE_NAME = 'ADD' AND F.FEED_ENTITY_ID = ?";

        List<Feed> feeds = jdbcTemplate.query(sql, FeedDbStorage::makeFeed, reviewId);
        if (feeds.size() != 1){
            return Optional.empty();
        }
        return Optional.of(feeds.get(0));
    }
}
