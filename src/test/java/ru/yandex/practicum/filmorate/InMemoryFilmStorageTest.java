package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

@SpringBootTest
class InMemoryFilmStorageTest {
    InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();

    @DisplayName("Добавление фильма с верными значениями полей")
    @Test
    void createFilm() {
        Film film = new Film("Терминатор", "Какой-то фильм",
                LocalDate.of(1995, 11, 15), 140);
        filmStorage.addOrUpdateFilm(film);
        Assertions.assertEquals(1, filmStorage.findAll().size());
    }

    @DisplayName("Добавление фильма с датой раньше, чем первый фильм")
    @Test
    void createFilmBefore1895() {
        Film film = new Film("Терминатор", "Какой-то фильм",
                LocalDate.of(1790, 11, 15), 140);
        Assertions.assertThrows(ValidationException.class, () -> filmStorage.addOrUpdateFilm(film), "Исключение, фильм не прошел валидацию");
    }

    @DisplayName("Добавление фильма без имени")
    @Test
    void createFilmWithEmptyName() {
        Film film = new Film(" ", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 140);
        Assertions.assertThrows(ValidationException.class, () -> filmStorage.addOrUpdateFilm(film), "Исключение, фильм не прошел валидацию");
    }

    @DisplayName("Добавление фильма с описанием длинной больше 200 символов")
    @Test
    void createFilmWithOver200Description() {
        Film film = new Film("Фильм для тестов", "Это описание больше 200 символов Это описание больше 200 символов " +
                "Это описание больше 200 символов Это описание больше 200 символов Это описание больше 200 символов " +
                "Это описание больше 200 символов Это описание больше 200 символов Это описание больше 200 символов " +
                "Это описание больше 200 символов Это описание больше 200 символов Это описание больше 200 символов " +
                "Это описание больше 200 символов Это описание больше 200 символов Это описание больше 200 символов " +
                "Это описание больше 200 символов Это описание больше 200 символов Это описание больше 200 символов " +
                "Это описание больше 200 символов Это описание больше 200 символов Это описание больше 200 символов ",
                LocalDate.of(2001, 11, 15), 140);
        Assertions.assertThrows(ValidationException.class, () -> filmStorage.addOrUpdateFilm(film), "Исключение, фильм не прошел валидацию");
    }

    @DisplayName("Добавление фильма с отрицательной длительностью")
    @Test
    void createFilmNegativeDuration() {
        Film film = new Film("Фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), -140);
        Assertions.assertThrows(ValidationException.class, () -> filmStorage.addOrUpdateFilm(film), "Исключение, фильм не прошел валидацию");
    }

    @DisplayName("Обновление фильма фильма без имени")
    @Test
    void updateFilm() {
        Film film = new Film("Фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 140);
        Film updatedFilm = new Film("Обновленный фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 130);
        filmStorage.addOrUpdateFilm(film);
        updatedFilm.setId(1);
        filmStorage.addOrUpdateFilm(updatedFilm);
        Assertions.assertEquals("Обновленный фильм для тестов", filmStorage.findAll().stream().toList().getFirst().getName());
    }

    @DisplayName("Добавление лайка к фильму")
    @Test
    void addLikeToFilm() {
        Film film = new Film("Фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 140);
        Film updatedFilm = new Film("Другой фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 130);
        filmStorage.addOrUpdateFilm(film);
        filmStorage.addOrUpdateFilm(updatedFilm);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 3);
        Assertions.assertEquals(3, film.getLikes().size(), "Количество лайков не соответсвтвует ожидаемому");
    }

    @DisplayName("Добавление лайка к фильму которого нет")
    @Test
    void addLikeToNullFilm() {
        Assertions.assertThrows(NotFoundException.class, () -> filmStorage.addLike(4, 1), "Исключение не прошло");
    }

    @DisplayName("Удаление лайка у фильма")
    @Test
    void deleteLikeFromFilm() {
        Film film = new Film("Фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 140);
        Film updatedFilm = new Film("Другой фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 130);
        filmStorage.addOrUpdateFilm(film);
        filmStorage.addOrUpdateFilm(updatedFilm);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 3);
        filmStorage.deleteLike(1, 2);
        Assertions.assertEquals(2, film.getLikes().size(), "Количество лайков не соответсвтвует ожидаемому");
    }

    @DisplayName("Удаление лайка у фильма которого нет")
    @Test
    void deleteLikeFromNullFilm() {
        Assertions.assertThrows(NotFoundException.class, () -> filmStorage.deleteLike(1, 2), "Исключение не прошло");
    }

    @DisplayName("Поиск самого популярного фильма")
    @Test
    void findPopularFilm() {
        Film film = new Film("Фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 140);
        Film updatedFilm = new Film("Другой фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 25), 130);
        filmStorage.addOrUpdateFilm(film);
        filmStorage.addOrUpdateFilm(updatedFilm);
        for (int i = 0; i < 4; i++) {
            filmStorage.addLike(1, i + 1);
        }
        for (int i = 0; i < 6; i++) {
            filmStorage.addLike(2, i + 1);
        }
        Assertions.assertEquals("Другой фильм для тестов", filmStorage.findPopularFilm(5).stream()
                .map(Film::getName)
                .toList().getFirst());

    }
}
