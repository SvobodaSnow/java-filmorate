package ru.yandex.practicum.storage.reviews;

import ru.yandex.practicum.model.reviews.Reviews;

import java.util.List;

public interface ReviewsStorage {
    Reviews addReviews(Reviews newReviews);

    Reviews updateReviews(Reviews reviews);

    Reviews updateUseful(Reviews reviews);

    Reviews getReviewsById(int id);

    void deleteReviewsById(int id);

    List<Reviews> getAllReviews();

    List<Reviews> getReviewsListByFlmId(int filmId, int count);
}
