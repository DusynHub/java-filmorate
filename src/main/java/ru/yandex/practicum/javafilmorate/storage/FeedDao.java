package ru.yandex.practicum.javafilmorate.storage;

import ru.yandex.practicum.javafilmorate.model.Feed;

import java.util.Collection;

public interface FeedDao {
    Collection<Feed> getFeed(Long userId, Integer limit);

    void addFeed(Feed feed);

    Feed findFeedByEntityId(Long reviewId);
}
