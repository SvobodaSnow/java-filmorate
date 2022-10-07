package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Feed;
import ru.yandex.practicum.storage.feed.FeedStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;

    public List<Feed> getFeedsByUserId(long userId) {
        return feedStorage.getFeedsByUserId(userId);
    }
}
