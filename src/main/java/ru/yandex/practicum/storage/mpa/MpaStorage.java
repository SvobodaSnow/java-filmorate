package ru.yandex.practicum.storage.mpa;

import ru.yandex.practicum.model.mpa.Mpa;

import java.util.List;

public interface MpaStorage {

    Mpa getMpaById(int id);

    List<Mpa> getMpa();
}
