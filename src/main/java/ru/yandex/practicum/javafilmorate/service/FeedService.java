package ru.yandex.practicum.javafilmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.User;
import ru.yandex.practicum.javafilmorate.storage.FeedDao;
import ru.yandex.practicum.javafilmorate.model.Feed;

import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
public class FeedService {
    private final FeedDao feedStorage;
    private final UserService userService;
    @Autowired
    public FeedService(FeedDao feedStorage, UserService userService) {
        this.feedStorage = feedStorage;
        this.userService = userService;
    }

    public void addFeed(String methodName, Object[] parametrs) {
        Feed newFeed;
        if (methodName.contains("like")) {
            newFeed = createFeed(methodName, (Long) parametrs[1], (Long) parametrs[0]);
        } else {
            newFeed = createFeed(methodName, (Long) parametrs[0], (Long) parametrs[1]);
        }
        feedStorage.addFeed(newFeed);
    }

    public Collection<Feed> getFeed(Long userId, Integer limit) {
        User user = userService.getUserFromStorage(userId);
        if (Objects.isNull(user)){
            throw new EntityDoesNotExistException(
                    String.format("Пользователь с  id %d не существует.", userId));
        }
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
            case "createReview":
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
