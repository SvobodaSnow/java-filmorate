package ru.yandex.practicum.storage.reviews;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.reviews.Reviews;
import ru.yandex.practicum.service.reviews.ReviewValidationService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Primary
@Component
public class ReviewsDbStorage implements ReviewsStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private ReviewValidationService reviewValidationService;

    public ReviewsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Reviews addReviews(Reviews newReviews) {
        reviewValidationService.checkReviews(newReviews);

        String sqlInsertReviews = "INSERT INTO reviews (content, isPositive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(
                sqlInsertReviews,
                newReviews.getContent(),
                newReviews.getIsPositive(),
                newReviews.getUserId(),
                newReviews.getFilmId()
        );

        String sqlId = "SELECT review_id FROM reviews ORDER BY review_id DESC LIMIT 1";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlId);
        if (reviewRows.next()) {
            newReviews.setReviewId(reviewRows.getInt("review_id"));
        }
        return getReviewsById(newReviews.getReviewId());
    }

    @Override
    public Reviews updateReviews(Reviews reviews) {
        getReviewsById(reviews.getReviewId());
        reviewValidationService.checkReviews(reviews);

        String sql = "UPDATE reviews SET content = ?, isPositive = ? WHERE review_id = ?";
        jdbcTemplate.update(
                sql,
                reviews.getContent(),
                reviews.getIsPositive(),
                reviews.getReviewId()
        );
        return getReviewsById(reviews.getReviewId());
    }

    @Override
    public Reviews updateUseful(Reviews reviews) {
        getReviewsById(reviews.getReviewId());
        reviewValidationService.checkReviews(reviews);

        String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, reviews.getUseful(), reviews.getReviewId());
        return getReviewsById(reviews.getReviewId());
    }

    @Override
    public Reviews getReviewsById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        SqlRowSet reviewsRow = jdbcTemplate.queryForRowSet(sql, id);
        Reviews reviews = new Reviews();
        if (reviewsRow.next()) {
            reviews.setReviewId(id);
            reviews.setContent(reviewsRow.getString("content"));
            reviews.setIsPositive(reviewsRow.getBoolean("isPositive"));
            reviews.setUserId(reviewsRow.getInt("user_id"));
            reviews.setFilmId(reviewsRow.getInt("film_id"));
            reviews.setUseful(reviewsRow.getInt("useful"));
        } else {
            log.error("Получен запрос на получение отзыва с ID " + id + ", которого нет в базе данных");
            throw new NotFoundException("Отзыва нет в списке");
        }
        return reviews;
    }

    @Override
    public void deleteReviewsById(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Reviews> getAllReviews() {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReviews(rs));
    }

    @Override
    public List<Reviews> getReviewsListByFlmId(int filmId, int count) {
        reviewValidationService.checkFilmId(filmId);
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReviews(rs), filmId, count);
    }

    private Reviews makeReviews(ResultSet rs) throws SQLException {
        Reviews reviews = new Reviews();
        reviews.setReviewId(rs.getInt("review_id"));
        reviews.setContent(rs.getString("content"));
        reviews.setIsPositive(rs.getBoolean("isPositive"));
        reviews.setUserId(rs.getInt("user_id"));
        reviews.setFilmId(rs.getInt("film_id"));
        reviews.setUseful(rs.getInt("useful"));
        return reviews;
    }
}
