package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.Films.FilmDbStorage;

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
    private final UserService userService;

    public Collection<Film> findAll() {
        return filmDbStorage.findAll();
    }

    protected static final LocalDate DAYOFFILMDATE = LocalDate.of(1895, 12, 28);
    protected static final Integer FILM_DESCRIPTION_MAXLENGTH = 200;
    private final Map<Integer, Film> films = new HashMap<>();


    public Film addFilm(Film film) {
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
        if (film.getMpa() == null) {
            log.warn("MPA не найден");
            throw new ValidationException("Не корректный МРА");
        }
        if (!mpaService.mpaExists(film.getMpa().getId())) {
            log.warn("Не корректный ИД = {} MPA", film.getMpa().getId());
            throw new ValidationException("Не корректный МРА");
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> genreService.genreCheck(genre.getId()));
        }


        Film newFilm = filmDbStorage.create(film);
        log.info("Фильм с названием " + film.getName() + " и ID = " + film.getId() + " добавлен");
        return newFilm;
    }

    public Film update(Film newFilm) {
        log.info("Обновляем на фильм {}", newFilm);
        Film oldFilm = filmInDbExist(newFilm.getId());
        log.info("ФИЛЬМ НАЙДЕН {}", oldFilm);
        if (newFilm.getName().isBlank()) {
            log.error("Название фильма пустое");
            throw new ValidationException("Название фильма пустое");
        } else oldFilm.setName(newFilm.getName());
        if (newFilm.getDescription().length() > FILM_DESCRIPTION_MAXLENGTH) {
            log.error("Длина описания фильма больше 200 символов");
            throw new ValidationException("Длина описания фильма больше 200 символов");
        } else oldFilm.setDescription(newFilm.getDescription());
        if (newFilm.getReleaseDate().isBefore(DAYOFFILMDATE)) {
            log.error("Дата релиза фильма до 28 декабря 1895");
            throw new ValidationException("Дата релиза фильма до 28 декабря 1895");
        } else oldFilm.setReleaseDate(newFilm.getReleaseDate());
        if (newFilm.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительным числом.");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        } else oldFilm.setDuration(newFilm.getDuration());
        if (newFilm.getMpa().getId() < 1 || newFilm.getMpa().getId() > 5) {
            log.error("MPA фильма должна быть 1-5");
        } else oldFilm.setMpa(newFilm.getMpa());
        oldFilm.setGenres(newFilm.getGenres());
        Film updFilm = filmDbStorage.update(oldFilm);
        log.info("Фильм с названием " + updFilm.getName() + " и ID = " + updFilm.getId() + " обновлен");
        return updFilm;
    }

    public Optional<Film> findById(int id) {
        Optional<Film> filmOptional = filmDbStorage.findById(id);
        if (filmOptional.isEmpty()) throw new NotFoundException("Фильм не найден");
        return filmOptional;
    }

    public void deleteFilm(int id) {
        log.info("Удаляем фильм {}", id);
        if (filmInDbExist(id) != null) {
            filmDbStorage.removeById(id);
            log.info("Фильм с ИД {} удален", id);
        }
    }

    public void addLike(Integer id, Integer userId) {
        filmInDbExist(id);
        userService.userInDbExist(userId);
        filmDbStorage.setLike(filmDbStorage.findById(id).orElse(null), userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        filmInDbExist(id);
        userService.userInDbExist(userId);
        filmDbStorage.unLike(filmDbStorage.findById(id).orElse(null), userId);
    }

    public Collection<Film> findPopularFilm(Integer count) {
        return filmDbStorage.findPopularFilms(count);
    }

    private Film filmInDbExist(Integer id) {
        Optional<Film> film = filmDbStorage.findById(id);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        return film.orElse(null);
    }
}
