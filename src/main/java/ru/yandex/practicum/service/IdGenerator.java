package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;

@Service
public class IdGenerator {
    private int idFilm = 0;
    private int idUser =0;

    public int generateIdFilm() {
        idFilm += 1;
        return idFilm;
    }

    public void setIdFilm(int idFilm) {
        if(idFilm >= this.idFilm) {
            this.idFilm = idFilm;
        }
    }

    public int generateIdUser() {
        idUser += 1;
        return idUser;
    }

    public void setIdUser(int idUser) {
        if(idUser >= this.idUser) {
            this.idUser = idUser;
        }
    }
}
