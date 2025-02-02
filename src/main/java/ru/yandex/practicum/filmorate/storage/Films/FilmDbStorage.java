package ru.yandex.practicum.filmorate.storage.Films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmDbStorage {
    Optional<Film> findById(Integer id);

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void removeById(Integer id);

    void setLike(Film film, Integer userId);

    void unLike(Film film, Integer userId);

    Collection<Film> findPopularFilms(Integer count);

    Collection<Film> popularWithParams(Integer count, String genreId, String year);

    Collection<Film> getCommon(Integer userId, Integer friendId);
}
