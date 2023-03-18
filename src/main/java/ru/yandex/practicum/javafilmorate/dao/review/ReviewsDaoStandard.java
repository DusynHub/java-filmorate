package ru.yandex.practicum.javafilmorate.dao.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.javafilmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Имплементация DAO-интерфейса работы с Review.
 * Имплементация для работы с базой данных через JDBC.
 *
 * @see ReviewsDao
 * @see Review
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewsDaoStandard implements ReviewsDao {

    private final JdbcTemplate jdbcTemplate;

    private final String sqlQueryGetReviewById = "SELECT id, film_id, user_id, content, is_positive, useful " +
            "FROM public.reviews " +
            "WHERE id = ?";

    private final String sqlUpdateReviewUseful = "UPDATE public.reviews " +
            "SET useful = ? " +
            "WHERE id = ?";

    @Override
    public Review findReviewById(Long id) {
        log.info("DAO: поиск отзыва по ID.");
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sqlQueryGetReviewById, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return review;
    }

    @Override
    public List<Review> findReviews(Long filmId, Integer count) {
        log.info("DAO: поиск отзывов удовлетворяющих условию.");
        String sqlFindReviews;
        List<Review> reviewList;
        try {
            if (filmId == null) {
                sqlFindReviews = "SELECT id, film_id, user_id, content, is_positive, useful " +
                        "FROM public.reviews " +
                        "ORDER BY useful DESC " +
                        "LIMIT ?";
                reviewList = jdbcTemplate.query(sqlFindReviews, this::mapRowToReview, count);
            } else {
                sqlFindReviews = "SELECT id, film_id, user_id, content, is_positive, useful " +
                        "FROM public.reviews " +
                        "WHERE film_id = ? " +
                        "ORDER BY useful DESC " +
                        "LIMIT ?";
                reviewList = jdbcTemplate.query(sqlFindReviews, this::mapRowToReview, filmId, count);
            }
        } catch (EmptyResultDataAccessException e) {
            reviewList = new ArrayList<>();
            ;
        }
        return reviewList;
    }

    @Override
    public Review save(Review review) {
        log.info("DAO: сохранение нового отзыва.");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        Long id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue();
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        log.info("DAO: обновление отзыва.");
        String sqlUpdateReview = "UPDATE public.reviews " +
                "SET content = ?, is_positive = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlUpdateReview,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return jdbcTemplate.queryForObject(sqlQueryGetReviewById,
                this::mapRowToReview,
                review.getReviewId());
    }

    @Override
    public boolean delete(Long id) {
        log.info("DAO: удаление отзыва.");
        String sqlDeleteReview = "DELETE FROM public.reviews " +
                "WHERE id = ?";
        return jdbcTemplate.update(sqlDeleteReview, id) > 0;
    }

    @Override
    public Review like(Long id, Long userId) {
        log.info("DAO: лайк отзыву с ID " + id + ".");
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sqlQueryGetReviewById, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        jdbcTemplate.update(sqlUpdateReviewUseful, review.getUseful() + 1, id);
        return jdbcTemplate.queryForObject(sqlQueryGetReviewById,
                this::mapRowToReview,
                review.getReviewId());
    }

    @Override
    public Review dislike(Long id, Long userId) {
        log.info("DAO: дизлайк отзыву с ID " + id + ".");
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sqlQueryGetReviewById, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        jdbcTemplate.update(sqlUpdateReviewUseful, review.getUseful() - 1, id);
        return jdbcTemplate.queryForObject(sqlQueryGetReviewById,
                this::mapRowToReview,
                review.getReviewId());
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        log.info("DAO: удаление лайка у отзыва с ID " + id + ".");
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sqlQueryGetReviewById, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        jdbcTemplate.update(sqlUpdateReviewUseful, review.getUseful() - 1, id);
        return true;
    }

    @Override
    public boolean deleteDislike(Long id, Long userId) {
        log.info("DAO: удаление дизлайка у отзыва с ID " + id + ".");
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sqlQueryGetReviewById, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        jdbcTemplate.update(sqlUpdateReviewUseful, review.getUseful() + 1, id);
        return true;
    }

    // преобразование строки ответа БД в объект Review
    private Review mapRowToReview(ResultSet resultSet, int rowNumber) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .content(resultSet.getString("content"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}
