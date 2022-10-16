package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;

@Service
public class IdGenerator {
    private int filmId = 0;
    private int userId =0;

    public int generateIdFilm() {
        filmId += 1;
        return filmId;
    }

    public void setFilmId(int filmId) {
        if(filmId >= this.filmId) {
            this.filmId = filmId;
        }
    }

    public int generateIdUser() {
        userId += 1;
        return userId;
    }

    public void setUserId(int userId) {
        if(userId >= this.userId) {
            this.userId = userId;
        }
    }
}

