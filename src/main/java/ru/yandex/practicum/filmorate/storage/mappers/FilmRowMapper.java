package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.Likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final MpaDbStorage mpaDbStorage;
    private final LikesDbStorage likesDbStorage;
    private final GenreService genreService;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("DURATION"));
        film.getLikes().addAll(likesDbStorage.getUsersLikes(film.getId()));
        film.setMpa(mpaDbStorage.get(rs.getInt("MPA_ID")));
        film.setGenres(List.copyOf(genreService.findFilmGenres(film.getId())));
        if (rs.getObject("Dr", Integer.class) == null) {
            film.setDirectors(new ArrayList<>());
        } else {
            Director director = Director.builder()
                    .id(rs.getInt("Dr"))
                    .build();
            film.setDirectors(List.of(director));
        }
        return film;
    }
}
