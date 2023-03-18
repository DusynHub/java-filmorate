package ru.yandex.practicum.javafilmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.model.Review;
import ru.yandex.practicum.javafilmorate.service.reviews.ReviewsService;

import javax.validation.Valid;
import java.util.List;

/**
 * Обработка эндпоинтов для работы с Review.
 *
 * @see Review
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewsController {

    private final ReviewsService reviewsService;

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        log.info("Controller: эндпоинт добавление нового отзыва");
        return reviewsService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        log.info("Controller: эндпоинт обновления отзыва");
        return reviewsService.updateReview(review);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review likeReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Controller: эндпоинт - лайк отзыву");
        return reviewsService.likeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review dislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Controller: эндпоинт - дизлайк отзыву");
        return reviewsService.dislikeReview(id, userId);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReview(@PathVariable Long id) {
        log.info("Controller: эндпоинт удаления отзыва");
        return reviewsService.deleteReview(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Controller: эндпоинт удаления лайка");
        return reviewsService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public boolean deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Controller: эндпоинт удаления дизлайка");
        return reviewsService.deleteDislike(id, userId);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        log.info("Controller: эндпоинт получения отзыва");
        return reviewsService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(defaultValue = "10") Integer count) {
        log.info("Controller: эндпоинт получения отзывов по условию");
        return reviewsService.getReviews(filmId, count);

    }

}
