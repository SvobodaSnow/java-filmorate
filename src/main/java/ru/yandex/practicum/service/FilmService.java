package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.Storages;
import ru.yandex.practicum.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage = Storages.getDefaultInMemoryFilmStorage();
    private final UserStorage userStorage = Storages.getDefaultInMemoryUserStorage();

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.addLike(user);
        return filmStorage.updateFilm(film);
    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.removeLike(user);
        return filmStorage.updateFilm(film);
    }

    public List<Film> returnMostPopularFilm(int maxMostPopularFilm) {
        List<Film> films = filmStorage.findAll();
        films.sort(new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.returnNumberLikes() - o1.returnNumberLikes();
            }
        });
        if (films.size() < maxMostPopularFilm) {
            maxMostPopularFilm = films.size();
        }
        List<Film> mostPopularFilm = films.stream().limit(maxMostPopularFilm).collect(Collectors.toList());
//        for (int i = 0; i < maxMostPopularFilm; i++) {
//            mostPopularFilm.add(films.get(i));
//        }

        return mostPopularFilm;
    }
}
