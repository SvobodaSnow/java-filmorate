package ru.yandex.practicum.storage.director;

import ru.yandex.practicum.model.director.Director;

import java.util.List;

public interface DirectorStorage {

    Director addDirector(Director director);

    Director updateDirector(Director director);

    List<Director> getDirectors();

    Director getDirectorById(int directorId);

    void deleteDirecterById(int id);
}
