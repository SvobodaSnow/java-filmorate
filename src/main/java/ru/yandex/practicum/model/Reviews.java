package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {
    private int reviewId;
    private String content;
    //@Value("null")
    @JsonProperty("isPositive")
    private Boolean isPositive;
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
