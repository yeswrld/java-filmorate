package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.util.ArrayList;
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

    public Genre findById(Integer id){
        if (!genreDbStorage.genreExist(id)){
            log.warn("Жанр с id={} не найден", id);
            throw new NotFoundException("Жанр не найден");
        }
        return genreDbStorage.findById(id);
    }

    public void genreCheck(Integer id){
        if (!genreDbStorage.genreExist(id)){
            log.warn("Жанр с id={} не найден", id);
            throw new NotFoundException("Жанр не найден");
        }
    }

    public List<Genre> findFilmGenres(Integer id){
        List<Integer> genreIds = genreDbStorage.genreIds(id);
        List<Genre> genres = new ArrayList<>();
        if (!genreIds.isEmpty()){
            genres = genreDbStorage.findFilmGenres(genreIds);
        }
        //log.info("Жанры {}", genres);
        return genres;
    }

}
