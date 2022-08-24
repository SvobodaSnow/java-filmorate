package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int rate;
    private Set<Integer> filmLikes;
    private Set<Genre> genres;
    private MPA mpa;

    public void addLike(User newLike) {
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
        }
        filmLikes.add(newLike.getId());
        rate += 1;
    }

    public void removeLike(User deletedLike) {
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
        }
        filmLikes.remove(deletedLike.getId());
        rate -= 1;
    }

    public void addLike(int newLike) {
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
        }
        filmLikes.add(newLike);
        rate += 1;
    }

    public void removeLike(int deletedLike) {
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
        }
        filmLikes.remove(deletedLike);
        rate -= 1;
    }

    public int returnNumberLikes() {
        return rate;
    }

    public void addGenre(Genre genre) {
        if (genres == null) {
            genres = new HashSet<>();
        }
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        if (genres == null) {
            genres = new HashSet<>();
        }
        genres.remove(genre);
    }
}
