package ru.yandex.practicum.javafilmorate.dao.review;

import ru.yandex.practicum.javafilmorate.model.Review;

import java.util.List;

public interface ReviewsDao {
    Review findReviewById(Long id);
    List<Review> findReviews(Long filmId, Integer count);
    Review save(Review review);
    Review update(Review review);
    boolean delete(Long id);
    Review like(Long id, Long userId);
    Review dislike(Long id, Long userId);
    boolean deleteLike(Long id, Long userId);
    boolean deleteDislike(Long id, Long userId);

}
