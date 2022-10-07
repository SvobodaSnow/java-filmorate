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
    private Set<Director> directors = new HashSet<>();
    private Mpa mpa;

    public void addLikeByUser(User user) {
        likedUsers.add(user.getId());
        rate += 1;
    }

    public void removeLikeByUser(User user) {
        likedUsers.remove(user.getId());
        rate -= 1;
    }

    public void addLikeByUserId(int userId) {
        likedUsers.add(userId);
        rate += 1;
    }

    public void removeLikeByUserId(int userId) {
        likedUsers.remove(userId);
        rate -= 1;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
    }
}
