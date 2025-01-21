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

import java.util.*;


@Component
public class FilmDbStorageImplementation extends BaseStorage<Film> implements FilmDbStorage {
    private final FilmRowMapper filmRowMapper;
    private final GenreService genreService;


    public FilmDbStorageImplementation(JdbcTemplate jdbc, FilmRowMapper filmRowMapper, GenreService genreService) {
        super(jdbc);
        this.filmRowMapper = filmRowMapper;
        this.genreService = genreService;
    }

    @Override
    public Optional<Film> findById(Integer id) {
        String findById = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID FROM FILMS WHERE ID = ?";
        try {
            Film result = findOne(filmRowMapper, findById, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }


    @Override
    public Collection<Film> findAll() {
        String findAllQ = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID from FILMS";
        return findMany(filmRowMapper, findAllQ);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("ID");
        Map<String, Object> param = new HashMap<>();
        param.put("name", film.getName());
        param.put("description", film.getDescription());
        param.put("release_date", film.getReleaseDate());
        param.put("duration", film.getDuration());
        param.put("MPA_ID", film.getMpa().getId());
        Number filmId = simpleJdbcInsert.executeAndReturnKey(param);
        film.setId(filmId.intValue());
        updateGenres(film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        String updQ = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE ID = ?";
        jdbc.update(updQ,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        updateGenres(newFilm);
        return newFilm;
    }

    @Override
    public void removeById(Integer id) {
        String removeGenresQ = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        update(removeGenresQ, id);
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
        String popularFilmQ = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID FROM FILMS AS F " +
                              "LEFT OUTER JOIN LIKES AS L ON F.ID = L.FILM_ID " +
                              "GROUP BY F.ID " +
                              "ORDER BY COUNT(L.FILM_ID) " +
                              "DESC " +
                              "LIMIT " + count;
        return jdbc.query(popularFilmQ, filmRowMapper);
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

}
