package ru.yandex.practicum.model.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feed {
    private long eventId;
    private long timestamp;
    private long userId;
    private String eventType;
    private String operation;
    private long entityId;
}