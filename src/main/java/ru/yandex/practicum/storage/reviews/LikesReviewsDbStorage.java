package ru.yandex.practicum.storage.reviews;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.servlet.http.PushBuilder;

@Slf4j
@Primary
@Component
public class LikesReviewsDbStorage implements LikesReviewsStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesReviewsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean checkLike(int reviewsId, int userId) {
        String sql = "SELECT * FROM likes_reviews WHERE review_id = ? AND user_id = ?";
        SqlRowSet reviewsRow = jdbcTemplate.queryForRowSet(sql, reviewsId, userId);
        return reviewsRow.next();
    }

    @Override
    public void addLikeReviews(int reviewsId, int userId) {
        String sql = "INSERT INTO likes_reviews (review_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, reviewsId, userId);
    }

    @Override
    public void deleteLikeReviews(int reviewsId, int userId) {
        String sql = "DELETE FROM likes_reviews WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewsId, userId);
    }
}
