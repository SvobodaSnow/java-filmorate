package ru.yandex.practicum.storage;

public class Storages {
    private static InMemoryFilmStorage inMemoryFilmStorage;
    private static InMemoryUserStorage inMemoryUserStorage;

    public static InMemoryFilmStorage getDefaultInMemoryFilmStorage() {
        if (inMemoryFilmStorage == null) {
            inMemoryFilmStorage = new InMemoryFilmStorage();
        }
        return inMemoryFilmStorage;
    }

    public static InMemoryUserStorage getDefaultInMemoryUserStorage() {
        if (inMemoryUserStorage == null) {
            inMemoryUserStorage = new InMemoryUserStorage();
        }
        return inMemoryUserStorage;
    }
}
