package ru.yandex.practicum.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmValidationService;
import ru.yandex.practicum.service.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
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
}
