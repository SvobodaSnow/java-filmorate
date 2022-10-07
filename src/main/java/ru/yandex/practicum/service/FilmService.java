package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;

    public Film addLike(int filmId, int userId) {
        return filmStorage.updateFilm(filmStorage.addLikeFilm(filmId, userId));
    }

    public Film removeLike(int filmId, int userId) {
        return filmStorage.updateFilm(filmStorage.removeLikeFilm(filmId, userId));
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
}
