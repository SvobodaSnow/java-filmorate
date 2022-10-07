package ru.yandex.practicum.storage.feed;

import ru.yandex.practicum.model.Feed;

import java.util.List;
import java.util.Optional;

public interface FeedStorage {
    List<Feed> getFeedsByUserId(long userId);

    void addFeed(long timestamp, long userId, Integer eventTypeId, Integer operationId, long entityId);

    Optional<Feed> getFeedByReviewId(long reviewId);
}
