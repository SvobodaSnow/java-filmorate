package ru.yandex.practicum.service;

public class IdGenerator {
    private int id = 0;

    public int generate() {
        id += 1;
        return id;
    }

    public void setId(int id) {
        if(id >= this.id) {
            this.id = id;
        }
    }
}
