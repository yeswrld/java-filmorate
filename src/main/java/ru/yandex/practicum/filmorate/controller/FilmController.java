package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Выводим все фильмы с базы");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Integer id) {
        log.info("Выводим фильм с ИД = {}", id);
        return filmService.findById(id);
    }

    @GetMapping(value = "/popular")
    public Collection<Film> popularWithParams(@RequestParam(name = "count", defaultValue = "10") Integer count,
                                              @RequestParam(name = "genreId", defaultValue = "%") String genreId,
                                              @RequestParam(name = "year", defaultValue = "%") String year) {
        log.info("Выводим список из {} популярных фильмов в жанре id={} за {} год", count, genreId, year);
        return filmService.popularWithParams(count, genreId, year);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody Film film) {
        log.info("Добавляем фильм {} в БД", film);
        filmService.addFilm(film);
        log.info("Фильм {} успешно добавлен в БД", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        filmService.update(film);
        return film;
    }

    @DeleteMapping("/{id}")
    public Film deleteFilm(@PathVariable Integer id) {
        log.info("Удаляем фильм с ИД = {}", id);
        Film film = filmService.findById(id);
        filmService.deleteFilm(id);
        log.info("Фильм с ИД = {} успешно удалён", id);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id,
                        @PathVariable Integer userId) {
        log.info("Добавляем лайк к фильму {} от пользователя {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Лайк успешно добавлен");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        log.info("Удаляем лайк у фильма {} от пользователя {}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Лайк успешно удален");
    }

}

