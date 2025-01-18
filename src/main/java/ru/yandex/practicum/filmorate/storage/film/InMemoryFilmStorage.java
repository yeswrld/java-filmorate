package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    protected static final LocalDate DAYOFFILMDATE = LocalDate.of(1895, 12, 28);
    protected static final Integer FILM_DESCRIPTION_MAXLENGTH = 200;
    private final Map<Integer, Film> films = new HashMap<>();
    private Film filmFromStorage;
    protected static final Comparator<Film> FILM_COMPARATOR = (new Comparator<Film>() {
        public int compare(Film o1, Film o2) {
            return o1.getLikes().size() - o2.getLikes().size();
        }
    });


    @Override
    public Optional<Film> findById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film addOrUpdateFilm(Film film) throws ValidationException {
        if (film.getId() == null) {
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
            if (film.getGenres().isEmpty()) {
                log.error("Жанр не найден");
                throw new ValidationException("Жанр не найден");
            }
            if (film.getMpa() == null){
                log.error("MPA не найден");
                throw new ValidationException("MPA не найден");
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
            } else if (film.getDescription().length() >= FILM_DESCRIPTION_MAXLENGTH) {
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

    @Override
    public void addLike(Integer id, Integer userId) {
        if (films.get(id) == null) {
            throw new NotFoundException("Фильма для лайка нет");
        }
        filmFromStorage = films.get(id);
        Set<Integer> likes = new HashSet<>();
        if (filmFromStorage.getLikes() != null) {
            likes = filmFromStorage.getLikes();
        }
        likes.add(userId);
        filmFromStorage.setLikes(likes);
        log.info("Добавлен лайк к фильму \"" + filmFromStorage.getName() + "\" от пользователя с ИД = " + userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        if (films.get(filmId) == null) {
            throw new NotFoundException("Фильма для удаления лайка нет");
        }
        filmFromStorage = films.get(filmId);
        if (filmFromStorage.getLikes() != null) {
            filmFromStorage.getLikes().remove(userId);
        }
        log.info("Удален лайк у фильма \"" + filmFromStorage.getName() + "\" пользователем с ИД = " + userId);
    }

    @Override
    public Collection<Film> findPopularFilm(Integer count) {
        return films.values().stream()
                .filter(film -> film.getLikes() != null && film.getLikes().size() > 0)
                .sorted(FILM_COMPARATOR.reversed())
                .limit(count)
                .collect(Collectors.toList());

    }

    public int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
