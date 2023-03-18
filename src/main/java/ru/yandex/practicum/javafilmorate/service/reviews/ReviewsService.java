package ru.yandex.practicum.javafilmorate.service.reviews;

import ru.yandex.practicum.javafilmorate.model.Review;

import java.util.List;

public interface ReviewsService {
    Review addReview(Review review);

    Review updateReview(Review review);

    Review likeReview(Long id, Long userId);

    Review dislikeReview(Long id, Long userId);

    boolean deleteReview(Long id);

    boolean deleteLike(Long id, Long userId);

    boolean deleteDislike(Long id, Long userId);

    Review getReviewById(Long id);

    List<Review> getReviews(Long filmId, Integer count);
}
