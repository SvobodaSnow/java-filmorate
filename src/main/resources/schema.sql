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

<<<<<<< HEAD
CREATE TABLE IF NOT EXISTS directors (
    director_id int PRIMARY KEY AUTO_INCREMENT,
    name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS director_to_films (
    director_id int NOT NULL,
    film_id int NOT NULL
);

=======
>>>>>>> develop
CREATE TABLE IF NOT EXISTS reviews (
    review_id int PRIMARY KEY AUTO_INCREMENT,
    content varchar NOT NULL,
    isPositive boolean,
    user_id int NOT NULL,
    film_id int NOT NULL,
    useful int DEFAULT 0
);

CREATE TABLE IF NOT EXISTS likes_reviews (
    review_id int NOT NULL,
    user_id int NOT NULL
);

CREATE TABLE IF NOT EXISTS dislikes_reviews (
    review_id int NOT NULL,
    user_id int NOT NULL
);