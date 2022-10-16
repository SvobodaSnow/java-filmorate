package ru.yandex.practicum.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.director.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.genre.Genre;
import ru.yandex.practicum.model.mpa.Mpa;
import ru.yandex.practicum.storage.director.DirectorStorage;
import ru.yandex.practicum.storage.film.DirectorToFilmsStorage;
import ru.yandex.practicum.storage.film.FilmStorage;
import ru.yandex.practicum.storage.film.GenreToFilmsStorage;
import ru.yandex.practicum.storage.genre.GenreStorage;
import ru.yandex.practicum.storage.likes.LikesStorage;
import ru.yandex.practicum.storage.feed.FeedStorage;
import ru.yandex.practicum.storage.mpa.MpaStorage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final FeedStorage feedStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final GenreToFilmsStorage genreToFilmsStorage;
    private final DirectorToFilmsStorage directorToFilmsStorage;

    public Film addLike(int filmId, int userId) {
        Film newFilm = filmStorage.updateFilm(likesStorage.addLikeFilm(filmId, userId));
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                userId, 1, 5, filmId);
        return newFilm;
    }

    public Film removeLike(int filmId, int userId) {
        Film newFilm = filmStorage.updateFilm(likesStorage.removeLikeFilm(filmId, userId));
        feedStorage.addFeed(Instant.parse(LocalDateTime.now() + "z").toEpochMilli(),
                userId, 1, 4, filmId);
        return newFilm;
    }

    public List<Film> returnMostPopularFilm(int maxMostPopularFilm) {
        List<Film> films = findAll();
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
            List<Film> films = filmStorage.getFilmOrderByYearAndGenre(count, genreId, year);;
            for (Film film : films) {
                likesStorage.fillFilmLikeListForFilm(film);
                genreStorage.fillGenresForFilm(film);
                directorStorage.fillDirectorsForFilm(film);
            }
            return films;
        }
        if (genreId == null && year == null) {
            return returnMostPopularFilm(count);
        }
        if (year == null) {
            List<Film> films = filmStorage.getFilmOrderByGenre(count, genreId);
            for (Film film : films) {
                likesStorage.fillFilmLikeListForFilm(film);
                genreStorage.fillGenresForFilm(film);
                directorStorage.fillDirectorsForFilm(film);
            }
            return films;
        }
        List<Film> films = filmStorage.getFilmOrderByYear(count, year);
        for (Film film : films) {
            likesStorage.fillFilmLikeListForFilm(film);
            genreStorage.fillGenresForFilm(film);
            directorStorage.fillDirectorsForFilm(film);
        }
        return films;
    }

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        for (Film film : films) {
            likesStorage.fillFilmLikeListForFilm(film);
            genreStorage.fillGenresForFilm(film);
            directorStorage.fillDirectorsForFilm(film);
        }
        return films;
    }

    public Film addFilm(Film film) {
        Film newFilm = filmStorage.addFilm(film);

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                if (!genreToFilmsStorage.checkGenreToFilm(genre.getId(), film.getId())) {
                    genreToFilmsStorage.addGenreToFilm(genre.getId(), film.getId());
                }
            }
        }

        Set<Director> directors = film.getDirectors();
        if (directors != null) {
            for (Director director : directors) {
                if (!directorToFilmsStorage.checkDirectorToFilm(director.getId(), film.getId())) {
                    directorToFilmsStorage.addDirectorToFilm(director.getId(), film.getId());
                }
            }
        }

        return getFilmById(newFilm.getId());
    }

    public Film updateFilm(Film film) {
        Film newFilm = filmStorage.updateFilm(film);

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            genreToFilmsStorage.deleteAllGenresToFilm(film.getId());
            for (Genre genre : genres) {
                if (!genreToFilmsStorage.checkGenreToFilm(genre.getId(), film.getId())) {
                    genreToFilmsStorage.addGenreToFilm(genre.getId(), film.getId());
                }
            }
        }

        Set<Director> directors = film.getDirectors();
        if (directors != null) {
            directorToFilmsStorage.deleteAllDirectorsToFilm(film.getId());
            for (Director director : directors) {
                if (!directorToFilmsStorage.checkDirectorToFilm(director.getId(), film.getId())) {
                    directorToFilmsStorage.addDirectorToFilm(director.getId(), film.getId());
                }
            }
        }

        return getFilmById(film.getId());
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        likesStorage.fillFilmLikeListForFilm(film);
        genreStorage.fillGenresForFilm(film);
        directorStorage.fillDirectorsForFilm(film);
        return film;
    }

    public Mpa getMPAById(int id) {
        return mpaStorage.getMpaById(id);
    }

    public List<Mpa> getMpa() {
        return mpaStorage.getMpa();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public void deleteFilmById(int filmId) {
        likesStorage.deleteLikeFilmById(filmId);
        filmStorage.deleteFilmById(filmId);
    }

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(int directorId) {
        return directorStorage.getDirectorById(directorId);
    }

    public void deleteDirecterById(int id) {
        directorStorage.deleteDirecterById(id);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortingParameter) {
        getDirectorById(directorId);
        List<Film> films;
        if (sortingParameter.equals("year")) {
            films = filmStorage.getFilmsByDirectorSortedByYear(directorId);
        } else if (sortingParameter.equals("likes")) {
            films = filmStorage.getFilmsByDirectorSortedByLikes(directorId);
        } else {
            throw new ValidationException("Неверный параметр сортировки");
        }
        for (Film film : films) {
            likesStorage.fillFilmLikeListForFilm(film);
            genreStorage.fillGenresForFilm(film);
            directorStorage.fillDirectorsForFilm(film);
        }
        return films;
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
        List<Film> films;
        if (isDirector && !isTitle) {
            films = filmStorage.getFilmsSearchByDirector(query);
            for (Film film : films) {
                likesStorage.fillFilmLikeListForFilm(film);
                genreStorage.fillGenresForFilm(film);
                directorStorage.fillDirectorsForFilm(film);
            }
            return films;
        } else if (!isDirector && isTitle) {
            films = filmStorage.getFilmsSearchByTitle(query);
            for (Film film : films) {
                likesStorage.fillFilmLikeListForFilm(film);
                genreStorage.fillGenresForFilm(film);
                directorStorage.fillDirectorsForFilm(film);
            }
            return films;
        } else {
            films = filmStorage.getFilmsSearchByDirectorAndTitle(query);
            for (Film film : films) {
                likesStorage.fillFilmLikeListForFilm(film);
                genreStorage.fillGenresForFilm(film);
                directorStorage.fillDirectorsForFilm(film);
            }
            return films;
        }
    }
}
