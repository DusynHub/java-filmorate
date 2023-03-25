package ru.yandex.practicum.javafilmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.Review;
import ru.yandex.practicum.javafilmorate.model.User;
import ru.yandex.practicum.javafilmorate.service.reviews.ReviewsService;
import ru.yandex.practicum.javafilmorate.storage.FeedDao;
import ru.yandex.practicum.javafilmorate.model.Feed;

import java.util.Collection;
import java.util.Objects;
@Service
@Slf4j
@AllArgsConstructor
public class FeedService {
    private final FeedDao feedStorage;
    private final UserService userService;
    private final ReviewsService reviewsService;

    public void addFeed(String methodName, Object[] parameters) {
        Feed newFeed;
        if (parameters[0] instanceof Review){
            Review review = (Review) parameters[0];
            if (methodName.equals("updateReview")){
                newFeed = createFeed(methodName, reviewsService.getReviewById(review.getReviewId()).getUserId(), review.getReviewId());
            } else {
                newFeed = createFeed(methodName, review.getUserId(), review.getReviewId());
            }
        } else {
            if (methodName.equals("likeFilm") || methodName.equals("deleteLikeFromFilm")) {
                newFeed = createFeed(methodName, (Long) parameters[1], (Long) parameters[0]);
            } else {
                newFeed = createFeed(methodName, (Long) parameters[0], (Long) parameters[1]);
            }
        }
        feedStorage.addFeed(newFeed);
        log.info("FeedService: Добавлена запись в журнал : {}", newFeed);
    }

    // добавляем запись в фид по удалению ревью
    public void addFeed(String methodName, Long reviewId) {
        // проверяем что была предыдущая запись в фид
        Feed feed = feedStorage.findFeedByEntityId(reviewId);
        Review review = reviewsService.getReviewById(reviewId);
        if (!Objects.isNull(feed) && !Objects.isNull(review)) {
            feed = createFeed(methodName, feed.getUserId(), feed.getEntityId());
            feedStorage.addFeed(feed);
            log.info("FeedService: Добавлена запись в журнал об удалении ревью: {}", reviewId);
        }
    }

    public Collection<Feed> getFeed(Long userId, Integer limit) {
        User user = userService.getUserFromStorage(userId);
        if (Objects.isNull(user)){
            throw new EntityDoesNotExistException(
                    String.format("Пользователь с  id %d не существует.", userId));
        }
        log.info("FeedService: Получена лента новостей для юзера: {}", user.getId());
        return feedStorage.getFeed(userId,limit);
    }

    private Feed createFeed(String methodName, Long userId, Long entityId) {

        User user = userService.getUserFromStorage(userId);

        Feed feed = Feed.builder().userId(user.getId()).entityId(entityId).build();

        switch (methodName) {
            case "addFriend":
                feed.setEventType("FRIEND");
                feed.setOperationType("ADD");
                break;
            case "deleteFriend":
                feed.setEventType("FRIEND");
                feed.setOperationType("REMOVE");
                break;
            case "likeFilm":
                feed.setEventType("LIKE");
                feed.setOperationType("ADD");
                break;
            case "deleteLikeFromFilm":
                feed.setEventType("LIKE");
                feed.setOperationType("REMOVE");
                break;
            case "addReview":
                feed.setEventType("REVIEW");
                feed.setOperationType("ADD");
                break;
            case "deleteReview":
                feed.setEventType("REVIEW");
                feed.setOperationType("REMOVE");
                break;
            case "updateReview":
                feed.setEventType("REVIEW");
                feed.setOperationType("UPDATE");
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return feed;
    }
}
