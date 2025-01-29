DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS mpa
(
id INTEGER NOT NULL AUTO_INCREMENT,
name CHARACTER VARYING NOT NULL,
CONSTRAINT MPA_PK PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS films
(
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar not null,
description varchar,
release_date date,
duration INTEGER,
mpa_id INTEGER REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS genres
(
id    INTEGER  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar
);

CREATE TABLE IF NOT EXISTS films_genres
(
film_id INTEGER REFERENCES FILMS (id),
genre_id INTEGER REFERENCES GENRES (id)
);

CREATE TABLE IF NOT EXISTS users
(
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
email varchar NOT NULL,
login varchar NOT NULL,
name varchar NOT NULL,
birthday date
);

CREATE TABLE IF NOT EXISTS likes
(
user_id INTEGER REFERENCES users(id),
film_id INTEGER REFERENCES films(id)
);

CREATE TABLE IF NOT EXISTS friends
(
user_id int REFERENCES USERS (id),
friend_id INTEGER REFERENCES USERS (id),
confirmed BOOLEAN
);

CREATE TABLE IF NOT EXISTS Reviews (
  reviewId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content VARCHAR(255),
  isPositive BOOLEAN,
  userId INTEGER,
  filmId INTEGER,
  useful INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS review_likes (
reviewId INTEGER REFERENCES Reviews(reviewId) ON DELETE CASCADE,
userId INTEGER REFERENCES users(id) ON DELETE CASCADE,
type varchar(10),
PRIMARY KEY (reviewId, userId)
)



