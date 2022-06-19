package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.*;
import ru.yandex.practicum.model.Film;

import java.time.Duration;
import java.time.LocalDate;

public class FilmControllerTest {
    FilmController filmController = new FilmController();

    @Test
    public void shouldCreateNewFilm() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 1", "Test Description", localDateTest, durationTest);
        try {
            Film newFilm = filmController.crete(film);
            film.setId(newFilm.getId());
            Assertions.assertEquals(film, newFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateNewFilmNullName() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 2", "Test Description", localDateTest, durationTest);
        film.setName(null);
        try {
            Film newFilm = filmController.crete(film);
            film.setId(newFilm.getId());
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(InvalidNameException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotCreateNewFilmLongDescription() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 4", "Test Description Description Description " +
                "Description Description Description Description Description Description Description Description " +
                "Description Description Description Description Description Description", localDateTest, durationTest);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(ExceedLengthDescriptionException.class, e.getClass());
        }
    }

    @Test
    public void shouldCreateNewFilmLongDescriptionTwoHundredSymbols() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 5", "Test Description Description Description " +
                "Description Description Description Description Description Description Description Description " +
                "Description Description Description Description Description Des", localDateTest, durationTest);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
            Assertions.assertEquals(film, newFilmOne);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateNewFilmIncorrectReleaseDateTime() {
        LocalDate localDateTest = LocalDate.of(1800, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 6", "Test Description", localDateTest, durationTest);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(InvalidReleaseDateException.class, e.getClass());
        }
    }

    @Test
    public void shouldCreateNewFilmStartReleaseDateTime() {
        LocalDate localDateTest = LocalDate.of(1895, 12, 28);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 7", "Test Description", localDateTest, durationTest);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
            Assertions.assertEquals(film, newFilmOne);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateNewFilmNegativeDuration() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(-125);
        Film film = new Film(10, "Test Name 8", "Test Description", localDateTest, durationTest);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(InvalidDurationException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotCreateNewFilmZeroDuration() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(0);
        Film film = new Film(10, "Test Name 9", "Test Description", localDateTest, durationTest);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(InvalidDurationException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateFilm() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 10", "Test Description", localDateTest, durationTest);
        Film updateFilm = new Film(10, "Test Name 10", "Test Update Description",
                localDateTest, durationTest);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
            Assertions.assertEquals(updateFilm, updateNewFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotUpdateFilmNullName() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 11", "Test Description", localDateTest, durationTest);
        Film updateFilm = new Film(10, "Test Name 11", "Test Update Description",
                localDateTest, durationTest);
        updateFilm.setName(null);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(InvalidNameException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotUpdateFilmLongDescription() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 12", "Test Description", localDateTest, durationTest);
        Film updateFilm = new Film(10, "Test Name 12", "Test Update Description Description " +
                "Description Description Description Description Description Description Description Description " +
                "Description Description Description Description Description Description Description Description",
                localDateTest, durationTest);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(ExceedLengthDescriptionException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateFilmLongDescriptionTwoHundredSymbols() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 13", "Test Description", localDateTest, durationTest);
        Film updateFilm = new Film(10, "Test Name 13", "Test Update Description Description " +
                "Description Description Description Description Description Description Description Description " +
                "Description Description Description Description Description Descript",
                localDateTest, durationTest);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
            Assertions.assertEquals(updateFilm, updateNewFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotUpdateFilmIncorrectDate() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 14", "Test Description", localDateTest, durationTest);
        Film updateFilm = new Film(10, "Test Name 14", "Test Update Description",
                LocalDate.of(1800, 5, 15), durationTest);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(InvalidReleaseDateException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateFilmStartDate() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 15", "Test Description", localDateTest, durationTest);
        Film updateFilm = new Film(10, "Test Name 15", "Test Update Description",
                LocalDate.of(1895, 12, 28), durationTest);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
            Assertions.assertEquals(updateFilm, updateNewFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotUpdateNewFilmNegativeDuration() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 16", "Test Description", localDateTest, durationTest);
        Film updateFilm = new Film(10, "Test Name 16", "Test Update Description",
                localDateTest, Duration.ofMinutes(-125));
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(InvalidDurationException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotUpdateNewFilmZeroDuration() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film film = new Film(10, "Test Name 17", "Test Description", localDateTest, durationTest);
        Film updateFilm = new Film(10, "Test Name 17", "Test Update Description",
                localDateTest, Duration.ofMinutes(0));
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            Assertions.assertEquals(InvalidDurationException.class, e.getClass());
        }
    }

    @Test
    public void MustGetListAllMovies() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Duration durationTest = Duration.ofMinutes(125);
        Film filmOne = new Film(10, "Test Name 18", "Test Description", localDateTest, durationTest);
        Film filmTwo = new Film(10, "Test Name 19", "Test Description", localDateTest, durationTest);
        try {
            Film newFilmOne = filmController.crete(filmOne);
            Film newFilmTwo = filmController.crete(filmTwo);
            filmOne.setId(newFilmOne.getId());
            filmTwo.setId(newFilmTwo.getId());
            Film[] films = {filmOne, filmTwo};
            Assertions.assertArrayEquals(films, filmController.findAll().toArray());
        } catch (InvalidNameException | ExceedLengthDescriptionException |
                InvalidReleaseDateException | InvalidDurationException e) {
            e.printStackTrace();
        }
    }
}
