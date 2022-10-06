drop table if exists films;
drop table if exists FRIENDS;
drop table if exists genre;
drop table if exists genre_to_films;
drop table if exists likes;
drop table if exists MPA;
drop table if exists unconfirmed_requests;
drop table if exists users;

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

//Этот кусок сделан для отладки в постмане.
/*
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE genre ALTER COLUMN genre_id RESTART WITH 1;
ALTER TABLE mpa ALTER COLUMN rating_id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;*/
