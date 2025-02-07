package ru.yandex.practicum.filmorate.storage.Films;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.FilmSearchRowMapper;

import java.util.*;


@Component
public class FilmDbStorageImplementation extends BaseStorage<Film> implements FilmDbStorage {
    private final FilmRowMapper filmRowMapper;
    private final GenreService genreService;
    private final FilmSearchRowMapper filmSearchRowMapper;


    public FilmDbStorageImplementation(JdbcTemplate jdbc, FilmRowMapper filmRowMapper, GenreService genreService, FilmSearchRowMapper filmSearchRowMapper) {
        super(jdbc);
        this.filmRowMapper = filmRowMapper;
        this.genreService = genreService;
        this.filmSearchRowMapper = filmSearchRowMapper;
    }

    @Override
    public Optional<Film> findById(Integer id) {
        String findById = """
                SELECT f.*,
                d.name as DIRECTOR_NAME
                FROM films f
                LEFT JOIN directors d ON f.director_id = d.id
                WHERE f.ID = ?
                """;
        try {
            Film result = findOne(filmRowMapper, findById, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }


    @Override
    public Collection<Film> findAll() {
        String findAllQ = """
                SELECT f.*,
                d.name as DIRECTOR_NAME
                FROM films f
                LEFT JOIN directors d ON f.director_id = d.id
                """;
        return findMany(filmRowMapper, findAllQ);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc).withTableName("FILMS").usingGeneratedKeyColumns("ID");
        Map<String, Object> param = new HashMap<>();
        param.put("name", film.getName());
        param.put("description", film.getDescription());
        param.put("release_date", film.getReleaseDate());
        param.put("duration", film.getDuration());
        param.put("MPA_ID", film.getMpa().getId());
        if (film.getDirectors() == null) {
            param.put("DIRECTOR_ID", null);
        } else if (!film.getDirectors().isEmpty()) {
            param.put("DIRECTOR_ID", film.getDirectors().getFirst().getId());
        }
        Number filmId = simpleJdbcInsert.executeAndReturnKey(param);
        film.setId(filmId.intValue());
        updateGenres(film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getDirectors() == null || newFilm.getDirectors().isEmpty()) {
            String updQ = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ?, DIRECTOR_ID = null WHERE ID = ?";
            jdbc.update(updQ, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
        } else {
            String updQ = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ?, DIRECTOR_ID = ? WHERE ID = ?";
            jdbc.update(updQ, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getDirectors().get(0).getId(), newFilm.getId());
        }

        updateGenres(newFilm);
        return newFilm;
    }

    @Override
    public void removeById(Integer id) {
        String removeGenresQ = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        delete(removeGenresQ, id);
        String removeLikesQ = "DELETE FROM LIKES WHERE FILM_ID = ?";
        delete(removeLikesQ, id);
        String removeQ = "DELETE FROM FILMS WHERE ID = ?";
        delete(removeQ, id);
    }

    @Override
    public void setLike(Film film, Integer userId) {
        String likeQ = """
                INSERT INTO LIKES (USER_ID, FILM_ID) VALUES (?, ?)
                """;
        jdbc.update(likeQ, userId, film.getId());
    }

    @Override
    public void unLike(Film film, Integer userId) {
        String likeQ = """
                DELETE FROM LIKES
                 WHERE USER_ID = ? AND FILM_ID = ?
                """;
        jdbc.update(likeQ, userId, film.getId());
    }

    @Override
    public Collection<Film> findPopularFilms(Integer count) {

        String popularFilmQ = """
                SELECT f.*,
                       mpa.*,
                       (SELECT COUNT(*) FROM likes WHERE likes.film_id = f.id) as like_count
                FROM films f
                JOIN mpa ON f.mpa_id = mpa.id
                ORDER BY like_count DESC
                LIMIT ?
                """;

        List<Film> films = findMany(filmRowMapper, popularFilmQ, count);

        return films;
    }

    @Override
    public List<Film> recommendedFilms(Integer id) {
        String recomQ = """
                WITH TargetUserLikes AS (
                    SELECT
                        film_id
                    FROM
                        likes
                    WHERE
                        user_id = ? -- Переданный ID целевого пользователя
                ),
                MaxSimilarUser AS (
                    SELECT
                        l.user_id AS similar_user_id,
                        COUNT(*) AS common_likes_count
                    FROM
                        likes l
                    JOIN
                        TargetUserLikes tul
                    ON
                        l.film_id = tul.film_id AND l.user_id != ?
                    GROUP BY
                        l.user_id
                    ORDER BY
                        common_likes_count DESC
                    LIMIT 1 -- Выбираем пользователя с максимальным количеством общих лайков
                ),
                Recommendations AS (
                    SELECT DISTINCT
                        msu.similar_user_id,
                        l.film_id
                    FROM
                        MaxSimilarUser msu
                    JOIN
                        likes l
                    ON
                        msu.similar_user_id = l.user_id
                    WHERE
                        NOT EXISTS (
                            SELECT 1
                            FROM TargetUserLikes tul
                            WHERE tul.film_id = l.film_id
                        )
                )
                SELECT
                    f.*
                FROM
                    Recommendations r
                JOIN
                    films f
                ON
                    r.film_id = f.id;
                """;
        return jdbc.query(recomQ, filmRowMapper, id, id);
    }

    @Override
    public Collection<Film> popularWithParams(Integer count, String genreId, String year) {
        Collection<Film> films;

        String popularFilmQ = "";
        if (!genreId.equals("%")) {
            popularFilmQ = """
                    SELECT f.*,
                           mpa.*,
                           d.name as DIRECTOR_NAME,
                           (SELECT COUNT(*) FROM likes WHERE likes.film_id = f.id) as like_count
                           FROM films f
                           JOIN mpa ON f.mpa_id = mpa.id
                           LEFT JOIN directors d ON f.director_id = d.id
                           WHERE EXISTS (SELECT 1 FROM FILMS_GENRES fg WHERE fg.film_id = f.id AND fg.genre_id LIKE ?)
                             AND FORMATDATETIME(f.RELEASE_DATE, 'YYYY') LIKE ?
                           ORDER BY like_count DESC
                           LIMIT ?
                    """;
            films = findMany(filmRowMapper, popularFilmQ, genreId, year, count);
        } else {
            popularFilmQ = """
                    SELECT f.*,
                           mpa.*,
                           d.name as DIRECTOR_NAME,
                           (SELECT COUNT(*) FROM likes WHERE likes.film_id = f.id) as like_count
                           FROM films f
                           JOIN mpa ON f.mpa_id = mpa.id
                           LEFT JOIN directors d ON f.director_id = d.id
                           WHERE FORMATDATETIME(f.RELEASE_DATE, 'YYYY') LIKE ?
                           ORDER BY like_count DESC
                           LIMIT ?
                    """;
            films = findMany(filmRowMapper, popularFilmQ, year, count);
        }

        return films;
    }

    private void updateGenres(Film film) {
        String deleteGenresQ = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbc.update(deleteGenresQ, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            StringBuilder updGenresQ = new StringBuilder("INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES");
            List<Object> params = new ArrayList<>();
            List<Genre> genres = new ArrayList<>(film.getGenres());
            for (int i = 0; i < film.getGenres().size(); i++) {
                updGenresQ.append("(?, ?)");
                if (i < film.getGenres().size() - 1) {
                    updGenresQ.append(", ");
                }
                params.add(film.getId());
                params.add(genres.get(i).getId());
            }
            jdbc.update(updGenresQ.toString(), params.toArray());
        }
    }

    public Collection<Film> sortedDirectorID(Integer directorId) {
        String filmQ = """
                SELECT f.*,
                d.name as DIRECTOR_NAME
                FROM films f
                LEFT JOIN directors d ON f.director_id = d.id
                WHERE f.DIRECTOR_ID = ?
                """;
        List<Film> films = findMany(filmRowMapper, filmQ, directorId);

        return films;
    }

    @Override
    public Collection<Film> getCommon(Integer userId, Integer friendId) {
        String popularFilmQ = """
                SELECT f.*,
                       mpa.*,
                       (SELECT COUNT(*) FROM likes WHERE likes.film_id = f.id) as like_count
                FROM films f
                JOIN mpa ON f.mpa_id = mpa.id
                JOIN likes l1 ON l1.film_id = f.id AND l1.user_id = ?
                JOIN likes l2 ON l2.film_id = f.id AND l2.user_id = ?
                ORDER BY like_count DESC
                """;

        return findMany(filmRowMapper, popularFilmQ, userId, friendId);
    }

    @Override
    public Collection<Film> searchFilms(String query, String by) {
        String sql;
        Object[] params;
        if (by.equalsIgnoreCase("title")) {
            sql = """
                    SELECT f.*, mpa.*, NULL as director_id, NULL as director_name
                    FROM films f JOIN mpa ON f.mpa_id = mpa.id
                    WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'))
                    ORDER BY (SELECT COUNT(*) FROM likes WHERE likes.film_id = f.id) DESC
                    """;
            params = new Object[]{query};
        } else if (by.equalsIgnoreCase("director")) {
            sql = """
                    SELECT f.*, mpa.*, d.id as director_id, d.name as director_name
                    FROM films f JOIN mpa ON f.mpa_id = mpa.id JOIN directors d ON f.DIRECTOR_ID = d.id
                    WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))
                    ORDER BY (SELECT COUNT(*) FROM likes WHERE likes.film_id = f.id) DESC
                    """;
            params = new Object[]{query};
        } else if (by.equalsIgnoreCase("director,title") || by.equalsIgnoreCase("title,director")) {
            sql = """
                    SELECT f.*, mpa.*, d.id as director_id, d.name as director_name
                    FROM films f JOIN mpa ON f.mpa_id = mpa.id
                    LEFT JOIN directors d ON f.DIRECTOR_ID = d.id
                    WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%')) OR LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))
                    ORDER BY (SELECT COUNT(*) FROM likes WHERE likes.film_id = f.id) DESC
                    """;
            params = new Object[]{query, query};
        } else {
            throw new IllegalArgumentException("Некорректное значение параметра by");
        }
        return findMany(filmSearchRowMapper, sql, params);
    }

}