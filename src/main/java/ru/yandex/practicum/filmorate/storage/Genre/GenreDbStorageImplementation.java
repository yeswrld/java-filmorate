package ru.yandex.practicum.filmorate.storage.Genre;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Component
@Primary
@RequiredArgsConstructor
public class GenreDbStorageImplementation implements GenreDbStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Genre> findAll() {
        String findAllQ = """
                SELECT ID,
                NAME FROM GENRES""";
        return jdbc.query(findAllQ, new GenreRowMapper());
    }

    @Override
    public Genre findById(Integer id) {
        String findByIdQ = """
                SELECT ID, NAME
                FROM GENRES
                WHERE ID = ?
                """;
        return jdbc.queryForObject(findByIdQ, new GenreRowMapper(), id);
    }

    @Override
    public boolean genreExist(Integer id) {
        String existsQ = "SELECT CASE WHEN EXISTS (SELECT * FROM GENRES WHERE ID = ?) THEN TRUE ELSE FALSE END";
        return Boolean.TRUE.equals(jdbc.queryForObject(existsQ, Boolean.class, id));
    }

    @Override
    public List<Integer> filmGenreSIds(Integer id) {
        String genreIdsQ = "SELECT GENRE_ID FROM FILMS_GENRES WHERE FILM_ID = ? ";
        List<Integer> genresIds = jdbc.queryForList(genreIdsQ, Integer.class, id);
        Collections.sort(genresIds);
        return genresIds;
    }

    @Override
    public List<Genre> findFilmGenres(Integer filmID) {
        String filmGenresQ = """
                SELECT g.ID, g.NAME
                FROM GENRES g
                JOIN FILMS_GENRES fg ON g.ID = FG.GENRE_ID
                WHERE fg.film_id IN (?)
                """;
        return jdbc.query(filmGenresQ, new GenreRowMapper(), filmID);
    }
}
