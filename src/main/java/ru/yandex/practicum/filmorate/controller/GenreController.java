package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Получен запрос всех жанров");
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre get(@PathVariable Integer id) {
        log.info("Получен запрос жанра с ИД = {}", id);
        return genreService.findById(id);
    }

    @GetMapping("/{id}/genres")
    public List<Genre> findFilmGenres(@PathVariable Integer id) {
        log.info("Получен запрос жанров фильма с ИД = {}", id);
        return genreService.findFilmGenres(id);
    }
}
