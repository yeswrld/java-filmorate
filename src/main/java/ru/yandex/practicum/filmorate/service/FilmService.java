package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addOrUpdateFilm(Film film) {
        filmStorage.addOrUpdateFilm(film);
    }

    public Film findById(int id) {
        Optional<Film> filmOptional = filmStorage.findById(id);
        if (filmOptional.isEmpty()) throw new NotFoundException("Фильм не найден");
        return filmOptional.get();
    }

    public void addLike(Integer id, Integer userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> findPopularFilm(Integer count) {
        return filmStorage.findPopularFilm(count);
    }
}
