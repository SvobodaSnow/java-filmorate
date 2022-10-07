package ru.yandex.practicum.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.service.FilmValidationService;
import ru.yandex.practicum.service.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Genre> genres = Map.ofEntries(
            Map.entry(1, new Genre(1, "Комедия")),
            Map.entry(2, new Genre(2, "Драма")),
            Map.entry(3, new Genre(3, "Мультфтльм")),
            Map.entry(4, new Genre(4, "Триллер")),
            Map.entry(5, new Genre(5, "Документальный")),
            Map.entry(6, new Genre(6, "Боевик"))
    );
    private final Map<Integer, Mpa> mpa = Map.ofEntries(
            Map.entry(1, new Mpa(1, "G")),
            Map.entry(2, new Mpa(2, "PG")),
            Map.entry(3, new Mpa(3, "PG-13")),
            Map.entry(4, new Mpa(4, "R")),
            Map.entry(5, new Mpa(5, "NC-17"))
    );
    @Autowired
    private FilmValidationService filmValidationService;
    @Autowired
    private IdGenerator idGenerator;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        filmValidationService.checkFilmName(film);
        filmValidationService.checkLengthDescription(film);
        filmValidationService.checkDateCreationFilm(film);
        filmValidationService.checkFilmDuration(film);
        film.setId(idGenerator.generateIdFilm());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmValidationService.checkMovieAvailability(films, film);
        filmValidationService.checkFilmName(film);
        filmValidationService.checkLengthDescription(film);
        filmValidationService.checkDateCreationFilm(film);
        filmValidationService.checkFilmDuration(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        filmValidationService.checkMovieAvailability(films, id);
        return films.get(id);
    }

    @Override
    public Film addLikeFilm(int filmId, int userId) {
        Film film = films.get(filmId);
        film.addLikeByUserId(userId);
        return films.put(filmId, film);
    }

    @Override
    public Film removeLikeFilm(int filmId, int userId) {
        Film film = films.get(filmId);
        film.removeLikeByUserId(userId);
        return films.put(filmId, film);
    }

    @Override
    public Film updateRateFilm(Film film) {
        return null;
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpa.get(id);
    }

    @Override
    public List<Mpa> getMpa() {
        return new ArrayList<>(mpa.values());
    }

    @Override
    public Genre getGenreById(int id) {
        return genres.get(id);
    }

    @Override
    public List<Genre> getGenres() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public void deleteFilmById(int filmId) {
        films.get(filmId);
    }

    @Override
    public void deleteLikeFilmById(int filmId) {
        films.get(filmId).getLikedUsers().clear();
    }

    @Override
    public Director addDirector(Director director) {
        return null;
    }

    @Override
    public Director updateDirector(Director director) {
        return null;
    }

    @Override
    public List<Director> getDirectors() {
        return null;
    }

    @Override
    public Director getDirectorById(int directorId) {
        return null;
    }

    @Override
    public void deleteDirecterById(int id) {

    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(int directorId) {
        return null;
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(int directorId) {
        return null;
    }
    
    public List<Film> getFilmOrderByYearAndGenre(Integer count, Integer genreId, Integer year) {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getFilmOrderByYear(Integer count, Integer year) {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getFilmOrderByGenre(Integer count, Integer genreId) {
        return new ArrayList<>(films.values());
    }
}
