package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {
    private int reviewId;
    private String content;
    private boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;

    public void addLike() {
        useful += 1;
    }

    public void deleteLike() {
        useful -= 1;
    }

    public void addDislike() {
        useful -=1;
    }

    public void deleteDislike() {
        useful += 1;
    }
}
