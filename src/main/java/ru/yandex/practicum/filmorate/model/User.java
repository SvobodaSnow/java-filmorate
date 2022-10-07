package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private String birthday;
}


