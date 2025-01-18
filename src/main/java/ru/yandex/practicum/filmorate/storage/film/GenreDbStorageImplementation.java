package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

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
        return jdbc.query(findAllQ, new BeanPropertyRowMapper<>(Genre.class));
    }

    @Override
    public Genre findById(Integer id) {
        String findByIdQ = """
                SELECT ID, NAME
                FROM GENRES
                WHERE ID = ?
                """;
        return jdbc.queryForObject(findByIdQ, new BeanPropertyRowMapper<>(Genre.class), id);
    }

    @Override
    public boolean genreExist(Integer id) {
        String existsQ = """
                SELECT CASE WHEN EXISTS
                (SELECT * FROM GENRES
                WHERE ID = ?)
                WHEN 'TRUE' ELSE 'FALSE' END
                """;
        return Boolean.TRUE.equals(jdbc.queryForObject(existsQ, Boolean.class, id));
    }

    @Override
    public List<Integer> genreIds(Integer id) {
        String genreIdsQ = """
                SELECT *
                FROM FILMS_GENRES
                WHERE FILM_ID = ?
                """;
        List<Integer> genresIds = jdbc.queryForList(genreIdsQ, Integer.class, id);
        Collections.sort(genresIds);
        return genresIds;
    }

    @Override
    public List<Genre> findFilmGenres(List<Integer> genreIds) {
        StringBuilder filmGenresQ = new StringBuilder("""
                SELECT * FROM GENRES
                WHERE ID IN (")
                """);
        for (int i = 0; i < genreIds.size(); i++) {
            filmGenresQ.append(genreIds.get(i));
            if (i < genreIds.size() - 1){
                filmGenresQ.append(", ");
            }else {
                filmGenresQ.append(") GROUP BY ID ORDER BY ID");
            }
        }
        return jdbc.query(filmGenresQ.toString(), new BeanPropertyRowMapper<>(Genre.class));
    }
}
