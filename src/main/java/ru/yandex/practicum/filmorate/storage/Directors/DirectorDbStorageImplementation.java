package ru.yandex.practicum.filmorate.storage.Directors;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Component
@Primary
@RequiredArgsConstructor
public class DirectorDbStorageImplementation implements DirectorDbStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Director findById(Integer id) {
        String getQ = "SELECT ID, NAME FROM DIRECTORS WHERE ID = ?";
        return jdbc.queryForObject(getQ, new DataClassRowMapper<>(), id);
    }

    @Override
    public Collection<Director> findAll() {
        String findAllQ = "SELECT ID, NAME FROM DIRECTORS";
        return jdbc.query(findAllQ, new DirectorRowMapper());
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("ID");
        Map<String, Object> param = new HashMap<>();
        param.put("name", director.getName());
        Number directorId = simpleJdbcInsert.executeAndReturnKey(param);
        director.setId(directorId.intValue());
        return director;
    }

    @Override
    public Director update(Director newDirector) {
        String updQ = "UPDATE DIRECTORS SET NAME = ? WHERE ID = ?";
        jdbc.update(updQ,
                newDirector.getName()
        );
        return newDirector;
    }

    @Override
    public void removeById(Integer id) {
        String removeDirectorsQ = "DELETE FROM DIRECTORS WHERE ID = ?";
        jdbc.update(removeDirectorsQ, id);
    }

    @Override
    public boolean directorsExist(Integer id) {
        String existsQ = "SELECT CASE WHEN EXISTS (SELECT * FROM DIRECTORS WHERE ID = ?) THEN TRUE ELSE FALSE END";
        return Boolean.TRUE.equals(jdbc.queryForObject(existsQ, Boolean.class, id));
    }
}


