package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.Feed;
import ru.yandex.practicum.service.FeedService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/users/{id}/feed")
    public List<Feed> getFeedsByUserId(@PathVariable("id") long userId) {
        return feedService.getFeedsByUserId(userId);
    }
}
