package ru.yandex.practicum.javafilmorate.service.reviews;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.dao.FilmDbStorageDao;
import ru.yandex.practicum.javafilmorate.dao.UserDbStorageDao;
import ru.yandex.practicum.javafilmorate.dao.review.ReviewsDao;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.Review;

import java.util.List;

/**
 * Имплементация интерфейса ReviewService.
 * Бизнес-логика работы с отзывами.
 *
 * @see ReviewsService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewsServiceStandard implements ReviewsService {

    private final ReviewsDao reviewsRepository;
    private final UserDbStorageDao userRepository;
    private final FilmDbStorageDao filmRepository;

    @Override
    public Review addReview(Review review) {
        log.info("Service: добавление нового отзыва.");
        if (userRepository.getUser(review.getUserId()) == null)
            throw new EntityDoesNotExistException("Не существует пользователя с ID " + review.getUserId());
        if (!filmRepository.doesFilmExist(review.getFilmId()))
            throw new EntityDoesNotExistException("Не существует фильма с ID + " + review.getFilmId());
        return reviewsRepository.save(review);
    }

    @Override
    public Review updateReview(Review review) {
        log.info("Service: редактирование отззыва.");
        if (reviewsRepository.findReviewById(review.getReviewId()) == null)
            throw new EntityDoesNotExistException("Не сущетсвует Review с ID " + review.getReviewId());
        return reviewsRepository.update(review);
    }

    @Override
    public Review likeReview(Long id, Long userId) {
        log.info("Service: лайк отзыву.");
        if (reviewsRepository.findReviewById(id) == null)
            throw new EntityDoesNotExistException("Не сущетсвует Review с ID " + id);
        return reviewsRepository.like(id, userId);
    }

    @Override
    public Review dislikeReview(Long id, Long userId) {
        log.info("Service: дизлайк отзыву.");
        if (reviewsRepository.findReviewById(id) == null)
            throw new EntityDoesNotExistException("Не сущетсвует Review с ID " + id);
        return reviewsRepository.dislike(id, userId);
    }

    @Override
    public boolean deleteReview(Long id) {
        log.info("Service: удаление отзыва.");
        if (reviewsRepository.findReviewById(id) == null)
            throw new EntityDoesNotExistException("Не сущетсвует Review с ID " + id);
        return reviewsRepository.delete(id);
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        log.info("Service: удаление лайка.");
        if (reviewsRepository.findReviewById(id) == null)
            throw new EntityDoesNotExistException("Не сущетсвует Review с ID " + id);
        return reviewsRepository.deleteLike(id, userId);
    }

    @Override
    public boolean deleteDislike(Long id, Long userId) {
        log.info("Service: удаление дизлайка.");
        if (reviewsRepository.findReviewById(id) == null)
            throw new EntityDoesNotExistException("Не сущетсвует Review с ID " + id);
        return reviewsRepository.deleteDislike(id, userId);
    }

    @Override
    public Review getReviewById(Long id) {
        log.info("Service: получение отзыва по ID.");
        Review review = reviewsRepository.findReviewById(id);
        if (review == null)
            throw new EntityDoesNotExistException("Не сущетсвует Review с ID " + id);
        return review;
    }

    @Override
    public List<Review> getReviews(Long filmId, Integer count) {
        log.info("Service: получение отзывов удовлетворяющих условиям.");
        if (filmId != null && !filmRepository.doesFilmExist(filmId))
            throw new EntityDoesNotExistException("Не существует фильма с ID + " + filmId);
        return reviewsRepository.findReviews(filmId, count);
    }
}
