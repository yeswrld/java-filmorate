INSERT INTO GENRES (name)
VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

INSERT INTO MPA (name)
VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES
('Игра в осьминога', 'Фильм, который корейцы еще не отсняли', '2024-10-15', 90, 5),
('Разделение', 'Сериал, который надо посмотреть', '2022-12-25', 50, 3);

INSERT INTO USERS (email, login, name, birthday)
VALUES
('yane@sobaka.ru', 'login', 'cat', '1990-10-15'),
('yane@gmail.ru', 'sadfsafd', 'nasafds', '1991-10-15');

INSERT INTO FILMS_GENRES (film_id, genre_id)
VALUES
('1', '1');