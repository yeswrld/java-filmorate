package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DirectorRowMapper implements RowMapper<Director> {
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = Director.builder().build();
        director.setId(rs.getInt("ID"));
        director.setName(rs.getString("NAME"));
        return director;
    }
}
