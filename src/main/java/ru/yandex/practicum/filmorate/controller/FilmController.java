package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final LocalDate DAY_OF_FILM_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        filmCheck(film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        filmCheck(film);
        return film;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;

    }


    private Film filmCheck(Film film) throws ValidationException {
        if (film.getId() == null) {
            if (film.getName().isBlank()) {
                log.error("Название фильма пустое");
                throw new ValidationException("Название фильма пустое");
            }
            if (film.getDescription().length() > 200) {
                log.error("Длина описания фильма больше 200 символов");
                throw new ValidationException("Длина описания фильма больше 200 символов");
            }
            if (film.getReleaseDate().isBefore(DAY_OF_FILM_DATE)) {
                log.error("Дата релиза фильма до 28 декабря 1895");
                throw new ValidationException("Дата релиза фильма до 28 декабря 1895");
            }
            if (film.getDuration() <= 0) {
                log.error("Продолжительность фильма должна быть положительным числом.");
                throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
            }
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Фильм с названием " + film.getName() + " и ID = " + film.getId() + " добавлен");
            return film;
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            if (film.getName().isBlank()) {
                log.error("Название фильма пустое");
                throw new ValidationException("Название фильма пустое");
            } else if (film.getDescription().length() >= 200) {
                log.error("Длина описания фильма больше 200 символов");
                throw new ValidationException("Длина описания фильма больше 200 символов");
            } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.error("Дата релиза фильма до 28 декабря 1895");
                throw new ValidationException("Дата релиза фильма до 28 декабря 1895");
            }
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setReleaseDate(film.getReleaseDate());
            oldFilm.setDuration(film.getDuration());
            log.info("Фильм с названием " + oldFilm.getName() + " и ID = " + oldFilm.getId() + " обновлен");
            return oldFilm;
        } else {
            log.error("Фильм с ID = " + film.getId() + " не найден");
            throw new NotFoundException("Фильм с указанным ИД не найден");
        }
    }
}

