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
    private Set<Integer> likedUsers = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;

    public void addLike(User newLike) {
        likedUsers.add(newLike.getId());
        rate += 1;
    }

    public void removeLike(User deletedLike) {
        likedUsers.remove(deletedLike.getId());
        rate -= 1;
    }

    public void addLike(int newLike) {
        likedUsers.add(newLike);
        rate += 1;
    }

    public void removeLike(int deletedLike) {
        likedUsers.remove(deletedLike);
        rate -= 1;
    }

    public int getNumberLikes() {
        return rate;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
    }
}
