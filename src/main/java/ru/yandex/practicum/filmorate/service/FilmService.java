package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmDbStorage filmDbStorage;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final GenreDbStorage genreDbStorage;

    public Collection<Film> findAll() {
        return filmDbStorage.findAll();
    }

    protected static final LocalDate DAYOFFILMDATE = LocalDate.of(1895, 12, 28);
    protected static final Integer FILM_DESCRIPTION_MAXLENGTH = 200;
    private final Map<Integer, Film> films = new HashMap<>();


    public Film addOrUpdateFilm(Film film) {

        if (film.getId() == null) {
            log.info("Создаем фильм {}", film);
            if (film.getName().isBlank()) {
                log.error("Название фильма пустое");
                throw new ValidationException("Название фильма пустое");
            }
            if (film.getDescription().length() > FILM_DESCRIPTION_MAXLENGTH) {
                log.error("Длина описания фильма больше 200 символов");
                throw new ValidationException("Длина описания фильма больше 200 символов");
            }
            if (film.getReleaseDate().isBefore(DAYOFFILMDATE)) {
                log.error("Дата релиза фильма до 28 декабря 1895");
                throw new ValidationException("Дата релиза фильма до 28 декабря 1895");
            }
            if (film.getDuration() <= 0) {
                log.error("Продолжительность фильма должна быть положительным числом.");
                throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
            }
            if (film.getMpa() == null){
                log.warn("MPA не найден");
                throw new ValidationException("Не корректный МРА");
            }
            if (!mpaService.mpaExists(film.getMpa().getId())) {
                log.warn("Не корректный ИД = {} MPA", film.getMpa().getId());
                throw new ValidationException("Не корректный МРА");
            }
            if (film.getGenres() == null){
                log.warn("Жанр не найден");
                throw new ValidationException("Не корректный МРА");
            }
            if (film.getGenres() != null ) {
                film.getGenres().forEach(genre -> {
                    if (!genreDbStorage.genreExist(genre.getId())){
                        throw new ValidationException("Жанр с указанным ИД не найден");
                    }
                });
            }

            film.setId(getNextId());
            Film newFilm = filmDbStorage.create(film);
            log.info("Фильм с названием " + film.getName() + " и ID = " + film.getId() + " добавлен");
            return newFilm;
        }
        if (film.getId() != null) {
            log.info("Фильм c ИД = {} найден для обновления", film.getId());
            Film oldFilm = filmInDbExist(film.getId());
            log.info(String.valueOf(oldFilm));
            log.info(String.valueOf(film));
            if (film.getName().isBlank()) {
                log.error("Название фильма пустое");
                throw new ValidationException("Название фильма пустое");
            } else if (film.getDescription().length() >= FILM_DESCRIPTION_MAXLENGTH) {
                log.error("Длина описания фильма больше 200 символов");
                throw new ValidationException("Длина описания фильма больше 200 символов");
            } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.error("Дата релиза фильма до 28 декабря 1895");
                throw new ValidationException("Дата релиза фильма до 28 декабря 1895");
            }
            if (film.getMpa() == null){
                log.warn("MPA не найден");
                throw new ValidationException("Не корректный МРА");
            }
            if (!mpaService.mpaExists(film.getMpa().getId())) {
                log.warn("Не корректный ИД = {} MPA", film.getMpa().getId());
                throw new ValidationException("Не корректный МРА");
            }
            if (film.getGenres() == null){
                log.warn("Жанр не найден");
                throw new ValidationException("Не корректный МРА");
            }
            if (film.getGenres() != null ) {
                film.getGenres().forEach(genre -> {
                    if (!genreDbStorage.genreExist(genre.getId())){
                        throw new ValidationException("Жанр с указанным ИД не найден");
                    }
                });
            }
            Film newFilm = filmDbStorage.update(film);
            return newFilm;
        } else {
            log.error("Фильм с ID = " + film.getId() + " не найден");
            throw new NotFoundException("Фильм с указанным ИД не найден");
        }


    }

    public Optional<Film> findById(int id) {
        Optional<Film> filmOptional = filmDbStorage.findById(id);
        if (filmOptional.isEmpty()) throw new NotFoundException("Фильм не найден");
        return filmOptional;
    }

//    public void addLike(Integer id, Integer userId) {
//        if (userStorage.findById(userId).isEmpty()) {
//            throw new NotFoundException("Пользователь не найден");
//        }
//        filmDbStorage.addLike(id, userId);
//    }
//
//    public void deleteLike(Integer id, Integer userId) {
//        if (userStorage.findById(userId).isEmpty()) {
//            throw new NotFoundException("Пользователь не найден");
//        }
//        filmDbStorage.deleteLike(id, userId);
//    }

    public Collection<Film> findPopularFilm(Integer count) {
        return filmDbStorage.findPopularFilms(count);
    }

    public int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private Film filmInDbExist(Integer id){
        Optional<Film> film = filmDbStorage.findById(id);
        if (film.isEmpty()){
            throw new NotFoundException("Фильм не найден");
        }
        return film.orElse(null);
    }
}
