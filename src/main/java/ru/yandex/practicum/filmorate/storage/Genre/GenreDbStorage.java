package ru.yandex.practicum.filmorate.storage.Genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreDbStorage {
    Collection<Genre> findAll();

    Genre findById(Integer id);

    boolean genreExist(Integer id);

    List<Integer> filmGenreSIds(Integer id);

    List<Genre> findFilmGenres(Integer filmID);

}
