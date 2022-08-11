package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> filmsLike;

    public void addLike(User newLike) {
        if (filmsLike == null) {
            filmsLike = new HashSet<>();
        }
        filmsLike.add(newLike.getId());
    }

    public void removeLike(User deletedLike) {
        if (filmsLike == null) {
            filmsLike = new HashSet<>();
        }
        filmsLike.remove(deletedLike.getId());
    }

    public int returnNumberLikes() {
        if (filmsLike == null) {
            filmsLike = new HashSet<>();
        }
        return filmsLike.size();
    }
}
