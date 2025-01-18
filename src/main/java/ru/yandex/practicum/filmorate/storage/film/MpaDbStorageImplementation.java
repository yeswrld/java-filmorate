package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
@Primary
@RequiredArgsConstructor
public class MpaDbStorageImplementation implements MpaDbStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Mpa> findAll() {
        String findAllQ = """
                SELECT ID, NAME
                FROM MPA
                """;
        return jdbc.query(findAllQ, new BeanPropertyRowMapper<>(Mpa.class));
    }

    @Override
    public Mpa get(Integer id) {
        String getQ = """
                SELECT ID, NAME
                FROM MPA
                WHERE ID = ?
                """;
        return jdbc.queryForObject(getQ, new BeanPropertyRowMapper<>(Mpa.class), id);
    }

    @Override
    public Mpa findById(Integer id) {
        String findByIdQ = """
                SELECT ID, NAME
                FROM MPA
                WHERE ID = ? 
                """;
        return jdbc.queryForObject(findByIdQ, new BeanPropertyRowMapper<>(Mpa.class), id);
    }

    @Override
    public boolean mpaExists(Integer id) {
        String mpaExistsQ = """
                SELECT CASE WHEN EXISTS
                (SELECT * FROM MPA WHERE ID = ?)
                THEN 'TRUE' ELSE 'FALSE' END
                """;
        return Boolean.TRUE.equals(jdbc.queryForObject(mpaExistsQ, Boolean.class, id));
    }
}
