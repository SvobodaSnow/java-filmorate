package ru.yandex.practicum.storage.reviews;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
public class DislikesReviewsDbStorage implements DislikesReviewsStorage {
    private final JdbcTemplate jdbcTemplate;

    public DislikesReviewsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean checkDislike(int reviewsId, int userId) {
        String sql = "SELECT * FROM dislikes_reviews WHERE review_id = ? AND user_id = ?";
        SqlRowSet reviewsRow = jdbcTemplate.queryForRowSet(sql, reviewsId, userId);
        return reviewsRow.next();
    }

    @Override
    public void addDislikeReviews(int reviewsId, int userId) {
        String sql = "INSERT INTO dislikes_reviews (review_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, reviewsId, userId);
    }

    @Override
    public void deleteDislikeReviews(int reviewsId, int userId) {
        String sql = "DELETE FROM dislikes_reviews WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewsId, userId);
    }
}
