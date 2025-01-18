package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Integer id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopular(@RequestParam(name = "count",
            defaultValue = "10") Integer count) {
        return filmService.findPopularFilm(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody Film film) {
        filmService.addOrUpdateFilm(film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        filmService.addOrUpdateFilm(film);
        return film;
    }

//    @PutMapping("/{id}/like/{userId}")
//    public void addLike(@PathVariable Integer id,
//                        @PathVariable Integer userId) {
//        filmService.addLike(id, userId);
//    }
//
//    @DeleteMapping("/{id}/like/{userId}")
//    public void deleteLike(@PathVariable Integer id,
//                           @PathVariable Integer userId) {
//        filmService.deleteLike(id, userId);
//    }

}

