CREATE TABLE IF NOT EXISTS films (
    film_id int PRIMARY KEY AUTO_INCREMENT,
    name varchar NOT NULL,
    description varchar,
    release_date date,
    duration int,
    rate int DEFAULT 0,
    rating_id int
);

CREATE TABLE IF NOT EXISTS likes (
    film_id int,
    user_id int
);

CREATE TABLE IF NOT EXISTS MPA (
    rating_id int PRIMARY KEY AUTO_INCREMENT,
    name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS genre_to_films (
    film_id int NOT NULL,
    genre_id int NOT NULL
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id int PRIMARY KEY AUTO_INCREMENT,
    name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id int PRIMARY KEY AUTO_INCREMENT,
    email varchar NOT NULL,
    login varchar NOT NULL,
    name varchar NOT NULL,
    birthday date
);

CREATE TABLE IF NOT EXISTS friends (
    user_id int NOT NULL,
    friend_id int NOT NULL
);

CREATE TABLE IF NOT EXISTS unconfirmed_requests (
    user_id int NOT NULL,
    friend_id int NOT NULL
);