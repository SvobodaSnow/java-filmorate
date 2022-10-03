package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Reviews;

import java.util.List;

public interface ReviewsStorage {
    Reviews addReviews(Reviews newReviews);

    Reviews updateReviews(Reviews reviews);

    Reviews getReviewsById(int id);

    void deleteReviewsById(int id);

    List<Reviews> getAllReviews();

    List<Reviews> getReviewsListByFlmId(int filmId, int count);

    void addLikeReviews(int reviewsId, int userId);

    void addDislikeReviews(int reviewsId, int userId);

    void deleteLikeReviews(int reviewsId, int userid);

    void deleteDislikeReviews(int reviewsId, int userId);
}
