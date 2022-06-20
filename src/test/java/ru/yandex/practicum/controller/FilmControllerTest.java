package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.*;
import ru.yandex.practicum.model.Film;

import java.time.LocalDate;

public class FilmControllerTest {
    FilmController filmController = new FilmController();

    @Test
    public void shouldCreateNewFilm() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 1", "Test Description", localDateTest, 125);
        try {
            Film newFilm = filmController.crete(film);
            film.setId(newFilm.getId());
            Assertions.assertEquals(film, newFilm);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateNewFilmNullName() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 2", "Test Description", localDateTest, 125);
        film.setName(null);
        try {
            Film newFilm = filmController.crete(film);
            film.setId(newFilm.getId());
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotCreateNewFilmLongDescription() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 4", "Test Description Description Description " +
                "Description Description Description Description Description Description Description Description " +
                "Description Description Description Description Description Description", localDateTest, 125);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldCreateNewFilmLongDescriptionTwoHundredSymbols() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 5", "Test Description Description Description " +
                "Description Description Description Description Description Description Description Description " +
                "Description Description Description Description Description Des", localDateTest, 125);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
            Assertions.assertEquals(film, newFilmOne);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateNewFilmIncorrectReleaseDateTime() {
        LocalDate localDateTest = LocalDate.of(1800, 5, 15);
        Film film = new Film(10, "Test Name 6", "Test Description", localDateTest, 125);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldCreateNewFilmStartReleaseDateTime() {
        LocalDate localDateTest = LocalDate.of(1895, 12, 28);
        Film film = new Film(10, "Test Name 7", "Test Description", localDateTest, 125);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
            Assertions.assertEquals(film, newFilmOne);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateNewFilmNegativeDuration() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 8", "Test Description", localDateTest, -125);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotCreateNewFilmZeroDuration() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 9", "Test Description", localDateTest, 0);
        try {
            Film newFilmOne = filmController.crete(film);
            film.setId(newFilmOne.getId());
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateFilm() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 10", "Test Description", localDateTest, 125);
        Film updateFilm = new Film(10, "Test Name 10", "Test Update Description",
                localDateTest, 125);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
            Assertions.assertEquals(updateFilm, updateNewFilm);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotUpdateFilmNullName() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 11", "Test Description", localDateTest, 125);
        Film updateFilm = new Film(10, "Test Name 11", "Test Update Description",
                localDateTest, 125);
        updateFilm.setName(null);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotUpdateFilmLongDescription() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 12", "Test Description", localDateTest, 125);
        Film updateFilm = new Film(10, "Test Name 12", "Test Update Description Description " +
                "Description Description Description Description Description Description Description Description " +
                "Description Description Description Description Description Description Description Description",
                localDateTest, 125);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateFilmLongDescriptionTwoHundredSymbols() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 13", "Test Description", localDateTest, 125);
        Film updateFilm = new Film(10, "Test Name 13", "Test Update Description Description " +
                "Description Description Description Description Description Description Description Description " +
                "Description Description Description Description Description Descript",
                localDateTest, 125);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
            Assertions.assertEquals(updateFilm, updateNewFilm);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotUpdateFilmIncorrectDate() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 14", "Test Description", localDateTest, 125);
        Film updateFilm = new Film(10, "Test Name 14", "Test Update Description",
                LocalDate.of(1800, 5, 15), 125);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateFilmStartDate() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 15", "Test Description", localDateTest, 125);
        Film updateFilm = new Film(10, "Test Name 15", "Test Update Description",
                LocalDate.of(1895, 12, 28), 125);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
            Assertions.assertEquals(updateFilm, updateNewFilm);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotUpdateNewFilmNegativeDuration() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 16", "Test Description", localDateTest, 125);
        Film updateFilm = new Film(10, "Test Name 16", "Test Update Description",
                localDateTest, -125);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotUpdateNewFilmZeroDuration() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film film = new Film(10, "Test Name 17", "Test Description", localDateTest, 125);
        Film updateFilm = new Film(10, "Test Name 17", "Test Update Description",
                localDateTest, 0);
        try {
            Film newFilm = filmController.crete(film);
            updateFilm.setId(newFilm.getId());
            Film updateNewFilm = filmController.update(updateFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals(ValidationException.class, e.getClass());
        }
    }

    @Test
    public void MustGetListAllMovies() {
        LocalDate localDateTest = LocalDate.of(2010, 5, 15);
        Film filmOne = new Film(10, "Test Name 18", "Test Description", localDateTest, 125);
        Film filmTwo = new Film(10, "Test Name 19", "Test Description", localDateTest, 125);
        try {
            Film newFilmOne = filmController.crete(filmOne);
            Film newFilmTwo = filmController.crete(filmTwo);
            filmOne.setId(newFilmOne.getId());
            filmTwo.setId(newFilmTwo.getId());
            Film[] films = {filmOne, filmTwo};
            Assertions.assertArrayEquals(films, filmController.findAll().toArray());
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
