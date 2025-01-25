package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreDbStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Collection<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    public Genre findById(Integer id) {
        if (!genreDbStorage.genreExist(id)) {
            log.info("Жанр не найден");
            throw new NotFoundException("Жанр не найден");
        }
        return genreDbStorage.findById(id);
    }

    public Boolean genreCheck(Integer id) {
        if (!genreDbStorage.genreExist(id)) {
            log.info("Жанр не найден");
            throw new NotFoundException("Жанр не найден");
        }
        return true;
    }

    public List<Genre> findFilmGenres(Integer id) {
        return genreDbStorage.findFilmGenres(id);

    }
}
