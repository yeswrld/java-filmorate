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
public class FilmSearchRowMapper implements RowMapper<Film> {

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
        film.setDuration(rs.getInt("duration"));
        film.getLikes().addAll(likesDbStorage.getUsersLikes(film.getId()));
        film.setMpa(mpaDbStorage.get(rs.getInt("mpa_id")));
        film.setGenres(List.copyOf(genreService.findFilmGenres(film.getId())));

        Integer directorId = rs.getObject("director_id", Integer.class);
        if (directorId != null) {
            Director director = Director.builder()
                    .id(directorId)
                    .name(rs.getString("director_name"))
                    .build();
            film.setDirectors(List.of(director));
        } else {
            film.setDirectors(new ArrayList<>());
        }

        return film;
    }
}
