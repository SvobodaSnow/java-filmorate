package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Film;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
public class FilmValidationService {
    public void checkFilmName (Film film) {
        if (film.getName() == null) {
            log.error("Получен запрос на добаление фильма без названия");
            throw new ValidationException("Имя фильма не указано");
        }

        if (film.getName().isBlank()) {
            log.error("Получен запрос на добаление фильма без названия");
            throw new ValidationException("Имя фильма не указано");
        }
    }

    public void checkLengthDescription(Film film) {
        if (film.getDescription().length() > 200) {
            log.error("Получен запрос на добавление фильма, описание которого больше 200 символов. Всего символов " +
                    film.getDescription().length());
            throw new ValidationException("Превышена длина описания");
        }
    }

    public void checkDateCreationFilm(Film film) {
        LocalDate localDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(localDate) || film.getReleaseDate() == null) {
            log.error("Получен запрос на добавление фильма раньше 28 декабря 1895 года. Дата " + film.getReleaseDate());
            throw new ValidationException("Не верная дата выпуска фильма");
        }
    }

    public void checkFilmDuration(Film film) {
        if (film.getDuration() <= 0) {
            log.error("Получен запрос на добавление фильма с отрицательной продолжительностью. Продолжительность " +
                    film.getDuration());
            throw new ValidationException("Продолжительность должна быть больше нуля");
        }
    }

    public void checkMovieAvailability(Map<Integer, Film> films, Film film) {
        if (films.get(film.getId()) == null) {
            log.error("Получен запрос на обновление фильма с несуществующим ID. ID " + film.getId());
            throw new ValidationException("Фильма нет в колекции");
        }
    }
}
