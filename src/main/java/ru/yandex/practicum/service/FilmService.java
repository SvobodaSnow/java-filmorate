package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;
import ru.yandex.practicum.storage.feed.FeedStorage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public Film addLike(int filmId, int userId) {
        Film newFilm = filmStorage.updateFilm(filmStorage.addLikeFilm(filmId, userId));
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                userId, 1, 5, filmId);
        return newFilm;
    }

    public Film removeLike(int filmId, int userId) {
        Film newFilm = filmStorage.updateFilm(filmStorage.removeLikeFilm(filmId, userId));
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                userId, 1, 4, filmId);
        return newFilm;
    }

    public List<Film> returnMostPopularFilm(int maxMostPopularFilm) {
        List<Film> films = filmStorage.findAll();
        films.sort(new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getRate() - o1.getRate();
            }
        });
        if (films.size() < maxMostPopularFilm) {
            maxMostPopularFilm = films.size();
        }
        return films.stream().limit(maxMostPopularFilm).collect(Collectors.toList());
    }

    public List<Film> returnFilmOrderByYearAndGenre(Integer count, Integer genreId, Integer year) {
        if (genreId != null && year != null) {
            return filmStorage.getFilmOrderByYearAndGenre(count, genreId, year);
        }
        if (genreId == null && year == null) {
            return returnMostPopularFilm(count);
        }
        if (year == null) {
            return filmStorage.getFilmOrderByGenre(count, genreId);
        }
        return filmStorage.getFilmOrderByYear(count, year);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Mpa getMPAById(int id) {
        return filmStorage.getMpaById(id);
    }

    public List<Mpa> getMpa() {
        return filmStorage.getMpa();
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public void deleteFilmById(int filmId) {
        filmStorage.deleteLikeFilmById(filmId);
        filmStorage.deleteFilmById(filmId);
    }

    public Director addDirector(Director director) {
        return filmStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return filmStorage.updateDirector(director);
    }

    public List<Director> getDirectors() {
        return filmStorage.getDirectors();
    }

    public Director getDirectorById(int directorId) {
        return filmStorage.getDirectorById(directorId);
    }

    public void deleteDirecterById(int id) {
        filmStorage.deleteDirecterById(id);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortingParameter) {
        getDirectorById(directorId);
        if (sortingParameter.equals("year")) {
            return filmStorage.getFilmsByDirectorSortedByYear(directorId);
        } else if (sortingParameter.equals("likes")) {
            return filmStorage.getFilmsByDirectorSortedByLikes(directorId);
        } else {
            throw new ValidationException("Неверный параметр сортировки");
        }
    }

    public List<Film> getFilmsBySearch(String query, String by) {
        boolean isDirector = false;
        boolean isTitle = false;
        for (String s : by.split(",")) {
            if (s.toLowerCase().equals("director")) {
                isDirector = true;
            } else if (s.toLowerCase().equals("title")) {
                isTitle = true;
            } else {
                throw new IllegalArgumentException("Некорректный атрибут query");
            }
        }
        if (isDirector && !isTitle) {
            return filmStorage.getFilmsSearchByDirector(query);
        } else if (!isDirector && isTitle) {
            return filmStorage.getFilmsSearchByTitle(query);
        } else {
            return filmStorage.getFilmsSearchByDirectorAndTitle(query);
        }
    }
}
