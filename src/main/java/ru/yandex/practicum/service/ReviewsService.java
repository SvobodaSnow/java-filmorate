package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Reviews;
import ru.yandex.practicum.storage.ReviewsDbStorage;
import ru.yandex.practicum.storage.ReviewsStorage;

import java.util.List;

@Service
public class ReviewsService {
    @Autowired
    private ReviewsStorage reviewsStorage;
    @Autowired
    private ReviewValidationService reviewValidationService;

    public Reviews addReviews (Reviews newReviews) {
        return reviewsStorage.addReviews(newReviews);
    }

    public Reviews updateReviews (Reviews reviews) {
        return reviewsStorage.updateReviews(reviews);
    }

    public Reviews getReviewsById(int id) {
        return reviewsStorage.getReviewsById(id);
    }

    public void deleteReviewsById(int id) {
        reviewsStorage.deleteReviewsById(id);
    }

    public List<Reviews> getReviewsList(int filmId, int count) {
        if (count <= 0) {
            throw new ValidationException("Передано неверное значение длинны списка");
        }
        List<Reviews> reviewsList;
        if (filmId == 0) {
            reviewsList = reviewsStorage.getAllReviews();
        } else {
            reviewsList = reviewsStorage.getReviewsListByFlmId(filmId, count);
        }
        return reviewsList;
    }

    public Reviews addLikeReviews(int reviewsId, int userId) {
        reviewValidationService.checkUserId(userId);
        Reviews reviews = reviewsStorage.getReviewsById(reviewsId);
        reviewsStorage.addLikeReviews(reviewsId, userId);
        reviews.addLike();
        return reviewsStorage.updateReviews(reviews);
    }

    public Reviews addDislikeReviews(int reviewsId, int userId) {
        reviewValidationService.checkUserId(userId);
        Reviews reviews = reviewsStorage.getReviewsById(reviewsId);
        reviewsStorage.addDislikeReviews(reviewsId, userId);
        reviews.addDislike();
        return reviewsStorage.updateReviews(reviews);
    }

    public Reviews deleteLikeReviews(int reviewsId, int userId) {
        reviewValidationService.checkUserId(userId);
        Reviews reviews = reviewsStorage.getReviewsById(reviewsId);
        reviewsStorage.deleteLikeReviews(reviewsId, userId);
        reviews.deleteLike();
        return reviewsStorage.updateReviews(reviews);
    }

    public Reviews deleteDislikeReviews(int reviewsId, int userId) {
        reviewValidationService.checkUserId(userId);
        Reviews reviews = reviewsStorage.getReviewsById(reviewsId);
        reviewsStorage.deleteDislikeReviews(reviewsId, userId);
        reviews.deleteDislike();
        return reviewsStorage.updateReviews(reviews);
    }
}
