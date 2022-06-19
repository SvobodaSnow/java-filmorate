package ru.yandex.practicum.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    int id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;

    public Film (int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
