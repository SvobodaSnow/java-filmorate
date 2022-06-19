package ru.yandex.practicum.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    int id;
    String email;
    String login;
    String name;
    LocalDate birthday;

    public User (int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
