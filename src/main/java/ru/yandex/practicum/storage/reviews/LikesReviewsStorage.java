package ru.yandex.practicum.storage.reviews;

public interface LikesReviewsStorage {
    boolean checkLike(int reviewsId, int userId);

    void addLikeReviews(int reviewsId, int userId);

    void deleteLikeReviews(int reviewsId, int userId);
}
