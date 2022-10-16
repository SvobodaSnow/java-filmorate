package ru.yandex.practicum.service.reviews;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.feed.Feed;
import ru.yandex.practicum.model.reviews.Reviews;
import ru.yandex.practicum.storage.reviews.DislikesReviewsStorage;
import ru.yandex.practicum.storage.reviews.LikesReviewsStorage;
import ru.yandex.practicum.storage.reviews.ReviewsStorage;
import ru.yandex.practicum.storage.feed.FeedStorage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewsService {
    private final ReviewsStorage reviewsStorage;
    private final ReviewValidationService reviewValidationService;
    private final FeedStorage feedStorage;
    private final LikesReviewsStorage likesReviewsStorage;
    private final DislikesReviewsStorage dislikesReviewsStorage;

    public Reviews addReviews (Reviews review) {
        Reviews newReview = reviewsStorage.addReviews(review);
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                review.getUserId(),
                2,
                5,
                newReview.getReviewId());
        return newReview;
    }

    public Reviews updateReviews (Reviews reviews) {
        Reviews newReview = reviewsStorage.updateReviews(reviews);
        Feed feed = feedStorage.getFeedByReviewId(reviews.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("Ревью с таким id отсутсвует"));
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                feed.getUserId(),
                2,
                6,
                reviews.getReviewId());
        return newReview;
    }

    public Reviews getReviewsById(int id) {
        return reviewsStorage.getReviewsById(id);
    }

    public void deleteReviewsById(int id) {
        Feed feed = feedStorage.getFeedByReviewId(id)
                .orElseThrow(() -> new IllegalArgumentException("Ревью с таким id отсутсвует"));
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                feed.getUserId(),
                2,
                4,
                feed.getEntityId());
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
        if (likesReviewsStorage.checkLike(reviewsId, userId)) {
            throw new ValidationException("Лайк уже поставлен");
        }
        if (dislikesReviewsStorage.checkDislike(reviewsId, userId)) {
            throw new ValidationException("Дизлайк уже поставлен");
        }
        likesReviewsStorage.addLikeReviews(reviewsId, userId);
        reviews.addLike();
        return reviewsStorage.updateUseful(reviews);
    }

    public Reviews addDislikeReviews(int reviewsId, int userId) {
        reviewValidationService.checkUserId(userId);
        Reviews reviews = reviewsStorage.getReviewsById(reviewsId);
        if (likesReviewsStorage.checkLike(reviewsId, userId)) {
            throw new ValidationException("Лайк уже поставлен");
        }
        if (dislikesReviewsStorage.checkDislike(reviewsId, userId)) {
            throw new ValidationException("Дизлайк уже поставлен");
        }
        dislikesReviewsStorage.addDislikeReviews(reviewsId, userId);
        reviews.addDislike();
        return reviewsStorage.updateUseful(reviews);
    }

    public Reviews deleteLikeReviews(int reviewsId, int userId) {
        reviewValidationService.checkUserId(userId);
        Reviews reviews = reviewsStorage.getReviewsById(reviewsId);
        if (!likesReviewsStorage.checkLike(reviewsId, userId)) {
            throw new ValidationException("Лайк отсутствует");
        }
        likesReviewsStorage.deleteLikeReviews(reviewsId, userId);
        reviews.deleteLike();
        return reviewsStorage.updateUseful(reviews);
    }

    public Reviews deleteDislikeReviews(int reviewsId, int userId) {
        reviewValidationService.checkUserId(userId);
        Reviews reviews = reviewsStorage.getReviewsById(reviewsId);
        if (!dislikesReviewsStorage.checkDislike(reviewsId, userId)) {
            throw new ValidationException("Лайк отсутствует");
        }
        dislikesReviewsStorage.deleteDislikeReviews(reviewsId, userId);
        reviews.deleteDislike();
        return reviewsStorage.updateUseful(reviews);
    }
}
