package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event.EventOperation;
import ru.yandex.practicum.filmorate.model.Event.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Events.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.Films.FilmDbStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    protected static final LocalDate DAYOFFILMDATE = LocalDate.of(1895, 12, 28);
    protected static final Integer FILM_DESCRIPTION_MAXLENGTH = 200;
    private final FilmDbStorage filmDbStorage;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final UserService userService;
    private final EventDbStorage eventDbStorage;

    public Collection<Film> findAll() {
        return filmDbStorage.findAll();
    }

    public Film addFilm(Film film) {
        filmValidate(film);
        Film newFilm = filmDbStorage.create(film);
        return newFilm;
    }

    public Film update(Film film) {
        filmValidateForUpdate(film);
        Film updFilm = filmDbStorage.update(film);
        return updFilm;
    }

    public Film findById(int id) {
        Film film = filmDbStorage.findById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        return film;
    }

    public void deleteFilm(int id) {
        Film filmFromDb = filmDbStorage.findById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        filmDbStorage.removeById(filmFromDb.getId());
    }

    public void addLike(Integer id, Integer userId) {
        userService.userInDbExist(userId);
        filmDbStorage.setLike(filmDbStorage.findById(id).orElseThrow(() -> new NotFoundException("Фильм не найден")), userId);
        eventDbStorage.add(EventType.LIKE, EventOperation.ADD, userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        userService.userInDbExist(userId);
        filmDbStorage.unLike(filmDbStorage.findById(id).orElseThrow(() -> new NotFoundException("Фильм не найден")), userId);
        eventDbStorage.add(EventType.LIKE, EventOperation.REMOVE, userId, id);
    }

    public Collection<Film> findPopularFilm(Integer count) {
        return filmDbStorage.findPopularFilms(count);
    }

    public Collection<Film> popularWithParams(Integer count, String genreId, String year) {
        return filmDbStorage.popularWithParams(count, genreId, year);
    }

    private Film filmValidate(Film film) {
        if (film.getName().isBlank()) {
            log.info("Название фильма пустое");
            throw new ValidationException("Название фильма пустое");
        }
        if (film.getDescription().length() > FILM_DESCRIPTION_MAXLENGTH) {
            log.info("Длина описания фильма больше 200 символов");
            throw new ValidationException("Длина описания фильма больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(DAYOFFILMDATE)) {
            log.info("Дата релиза фильма до 28 декабря 1895");
            throw new ValidationException("Дата релиза фильма до 28 декабря 1895");
        }
        if (film.getDuration() <= 0) {
            log.info("Продолжительность фильма должна быть положительным числом.");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
        if (film.getMpa() == null) {
            log.info("Не корректный МРА");
            throw new ValidationException("Не корректный МРА");
        }
        if (!mpaService.mpaExists(film.getMpa().getId())) {
            throw new NotFoundException("Не корректный МРА");
        }
        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < film.getGenres().size(); i++) {
                if (!genres.contains(film.getGenres().get(i))) {
                    genres.add(film.getGenres().get(i));
                }
            }
            film.setGenres(genres);
            film.getGenres().forEach(genre -> genreService.genreCheck(genre.getId()));
        }
        return film;
    }

    public Film filmValidateForUpdate(Film film) {
        Film oldFilm = filmDbStorage.findById(film.getId()).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма пустое");
        } else oldFilm.setName(film.getName());
        if (film.getDescription().length() > FILM_DESCRIPTION_MAXLENGTH) {
            throw new ValidationException("Длина описания фильма больше 200 символов");
        } else oldFilm.setDescription(film.getDescription());
        if (film.getReleaseDate().isBefore(DAYOFFILMDATE)) {
            throw new ValidationException("Дата релиза фильма до 28 декабря 1895");
        } else oldFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        } else oldFilm.setDuration(film.getDuration());
        if (!mpaService.mpaExists(film.getMpa().getId())) {
            oldFilm.setMpa(film.getMpa());
        }
        oldFilm.setGenres(film.getGenres());

        return oldFilm;
    }


    public Collection<Film> sortedDirectorID(Integer directorID, String sorBy) {
        Comparator<Film> explicitComparator2 = (film1, film2) -> film1.getLikes().size() - film2.getLikes().size();
        Comparator<Film> explicitComparator = (film1, film2) -> film1.getReleaseDate().compareTo(film2.getReleaseDate());
        Collection<Film> films = filmDbStorage.sortedDirectorID(directorID);
        if (sorBy.equals("likes")) {
            return films.stream()
                    .sorted(explicitComparator2.reversed())
                    .collect(Collectors.toList());
        } else if (sorBy.equals("year")) {
            return films.stream()
                    .sorted(explicitComparator)
                    .collect(Collectors.toList());
        } else {
            throw new ValidationException("Некоректный параметр сортировки.");
        }

    }


}
