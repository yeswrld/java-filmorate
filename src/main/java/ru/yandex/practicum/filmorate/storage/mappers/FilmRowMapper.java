package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film result = Film.builder().id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(
                        rs.getDate("release_date") != null
                                ? rs.getDate("release_date").toLocalDate()
                                : null
                )
                .duration(rs.getInt("duration"))
                .build();

        Integer mpaOnDb = rs.getInt("mpa_id");
        if (mpaOnDb != null) {
            result.setMpa(Mpa.builder()
                    .id(mpaOnDb)
                    .name(rs.getString("mpa_name"))
                    .build());
        }
        return result;
    }
}
