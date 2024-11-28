package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
class FilmControllerTest {
    FilmController filmController = new FilmController();

    @DisplayName("Добавление фильма с верными значениями полей")
    @Test
    void createFilm() {
        Film film = new Film("Терминатор", "Какой-то фильм",
                LocalDate.of(1995, 11, 15), 140);
        filmController.create(film);
        Assertions.assertEquals(1, filmController.findAll().size());
    }

    @DisplayName("Добавление фильма с датой раньше, чем первый фильм")
    @Test
    void createFilmBefore1895() {
        Film film = new Film("Терминатор", "Какой-то фильм",
                LocalDate.of(1790, 11, 15), 140);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film)
                , "Исключение, фильм не прошел валидацию");
    }

    @DisplayName("Добавление фильма без имени")
    @Test
    void createFilmWithEmptyName() {
        Film film = new Film(" ", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 140);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film)
                , "Исключение, фильм не прошел валидацию");
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
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film)
                , "Исключение, фильм не прошел валидацию");
    }

    @DisplayName("Добавление фильма с отрицательной длительностью")
    @Test
    void createFilmNegativeDuration() {
        Film film = new Film("Фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), -140);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film)
                , "Исключение, фильм не прошел валидацию");
    }

    @DisplayName("Обновление фильма фильма без имени")
    @Test
    void updateFilm() {
        Film film = new Film("Фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 140);
        Film updatedFilm = new Film("Обновленный фильм для тестов", "Какой-то фильм",
                LocalDate.of(2001, 11, 15), 130);
        filmController.create(film);
        updatedFilm.setId(1);
        filmController.update(updatedFilm);
        Assertions.assertEquals("Обновленный фильм для тестов"
                , filmController.findAll().stream().toList().getFirst().getName());
    }

}
