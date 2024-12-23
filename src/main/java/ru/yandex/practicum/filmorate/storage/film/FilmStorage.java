package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film addOrUpdateFilm(Film film);

    Optional<Film> findById(int id);

    Collection<Film> findAll();

    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    Collection<Film> findPopularFilm(Integer count);
}
