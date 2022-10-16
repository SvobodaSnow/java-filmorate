package ru.yandex.practicum.controller.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.feed.Feed;
import ru.yandex.practicum.service.feed.FeedService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/users/{id}/feed")
    public List<Feed> getFeedsByUserId(@PathVariable("id") long userId) {
        log.info("Получен запрос для вывода ленты событий пользователя с id = " + userId);
        return feedService.getFeedsByUserId(userId);
    }
}
