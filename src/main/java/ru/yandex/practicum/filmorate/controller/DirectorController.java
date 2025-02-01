package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/{id}")
    public Director findById(@PathVariable Integer id) {
        log.info("Выводим режиссера с ИД = {}", id);
        return directorService.findById(id);
    }

    @GetMapping
    public Collection<Director> findAll() {
        log.info("Выводим всех режиссеров с базы");
        return directorService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@RequestBody Director director) {
        log.info("Добавляем режисера {} в БД", director);
        directorService.create(director);
        log.info("Ружиссер {} успешно добавлен в БД", director);
        return director;
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        directorService.update(director);
        return director;
    }

    @DeleteMapping("/{id}")
    public Director deleteDirector(@PathVariable Integer id) {
        log.info("Удаляем режиссера с ИД = {}", id);
        Director director = directorService.findById(id);
        directorService.removeById(id);
        log.info("Режиссер с ИД = {} успешно удалён", id);
        return director;
    }
}
