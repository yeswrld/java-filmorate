package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.data.relational.core.sql.In;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreDbStorage {
    Collection<Genre> findAll();
    Genre findById(Integer id);
    boolean genreExist(Integer id);
    List<Integer> genreIds(Integer id);
    List<Genre> findFilmGenres(List<Integer> genreIds);

}
