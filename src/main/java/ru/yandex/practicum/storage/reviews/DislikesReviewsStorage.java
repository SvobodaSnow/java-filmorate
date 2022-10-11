package ru.yandex.practicum.storage.reviews;

public interface DislikesReviewsStorage {
    boolean checkDislike(int reviewsId, int userId);

    void addDislikeReviews(int reviewsId, int userId);

    void deleteDislikeReviews(int reviewsId, int userId);
}
