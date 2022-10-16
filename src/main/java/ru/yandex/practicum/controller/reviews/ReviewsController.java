package ru.yandex.practicum.controller.reviews;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.reviews.Reviews;
import ru.yandex.practicum.service.reviews.ReviewsService;

import java.util.List;

@Slf4j
@RestController
public class ReviewsController {
    @Autowired
    private ReviewsService reviewsService;

    @PostMapping("/reviews")
    public Reviews create(@RequestBody Reviews reviews) {
        Reviews newReviews = reviewsService.addReviews(reviews);
        log.info("Отзыв добавлен с ID " + reviews.getReviewId());
        return newReviews;
    }

    @PutMapping("/reviews")
    public Reviews update(@RequestBody Reviews reviews) {
        Reviews newReviews = reviewsService.updateReviews(reviews);
        log.info("Отзыв с ID " + newReviews.getReviewId() + " обновлен");
        return newReviews;
    }

    @GetMapping("/reviews/{id}")
    public Reviews getReviewsById(@PathVariable int id) {
        Reviews reviews = reviewsService.getReviewsById(id);
        log.info("Отзыв с ID " + id + " успешно получен");
        return reviews;
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReviewsById(@PathVariable int id) {
        reviewsService.deleteReviewsById(id);
        log.info("Отзыв успешно c id = " + id + " успешно удален");
    }

    @GetMapping("/reviews")
    public List<Reviews> getReviewsForFilm(@RequestParam(defaultValue = "0") int filmId,
                                           @RequestParam(defaultValue = "10") int count) {
        List<Reviews> reviewsList = reviewsService.getReviewsList(filmId, count);
        log.info("Список отзывов успешно сформирован");
        return reviewsList;
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public Reviews addLikeReviews(@PathVariable int id, @PathVariable int userId) {
        Reviews reviews = reviewsService.addLikeReviews(id, userId);
        log.info("Получен запрос на добавление лайка к коментарию с ID " + id);
        return reviews;
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public Reviews addDislikeReviews(@PathVariable int id, @PathVariable int userId) {
        Reviews reviews = reviewsService.addDislikeReviews(id, userId);
        log.info("Получен запрос на добавление дизлайка к коментарию с ID " + id);
        return reviews;
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public Reviews deleteLikeReviews(@PathVariable int id, @PathVariable int userId) {
        Reviews reviews = reviewsService.deleteLikeReviews(id, userId);
        log.info("Лайк успешно удален");
        return reviews;
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public Reviews deleteDislikeReviews(@PathVariable int id, @PathVariable int userId) {
        Reviews reviews = reviewsService.deleteDislikeReviews(id, userId);
        log.info("Дизлайк успешно удален");
        return reviews;
    }
}
