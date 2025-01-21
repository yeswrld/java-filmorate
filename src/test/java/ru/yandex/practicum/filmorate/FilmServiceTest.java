package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Films.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmServiceTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void findFilmById() {
        Mpa mpa = new Mpa();
        mpa.setId(5);
        mpa.setName("NC-17");
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        List<Genre> genres = List.of(genre);
        Film film = new Film();
        film.setId(1);
        film.setName("Игра в осьминога");
        film.setDescription("Фильм, который корейцы еще не отсняли");
        film.setReleaseDate(LocalDate.of(2024, 10, 15));
        film.setDuration(90);
        film.setMpa(mpa);
        film.setGenres(genres);
        Film filmFromDb = filmDbStorage.findById(1).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Assertions.assertEquals(film, filmFromDb);
    }

    @Test
    public void getAllFromFilms() {
        Assertions.assertEquals(2, filmDbStorage.findAll().size());
    }

    @Test
    public void updateFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(5);
        mpa.setName("NC-17");
        Genre genre = new Genre();
        genre.setId(6);
        genre.setName("Боевик");
        List<Genre> genres = List.of(genre);
        Film film = new Film();
        film.setId(1);
        film.setName("Терминатор");
        film.setDescription("Старый фильм");
        film.setReleaseDate(LocalDate.of(2000, 10, 15));
        film.setDuration(120);
        film.setMpa(mpa);
        film.setGenres(genres);
        Film filmFromDb = filmDbStorage.update(film);
        assertThat(filmFromDb)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("description", "Старый фильм")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 10, 15))
                .hasFieldOrPropertyWithValue("duration", 120)
                .hasFieldOrPropertyWithValue("mpa", mpa)
                .hasFieldOrPropertyWithValue("genres", genres);
    }

    @Test
    public void removeFromFilmsById() {
        filmDbStorage.removeById(1);
        Assertions.assertEquals(1, filmDbStorage.findAll().size());
    }

    @Test
    public void setAndUnsetLike() {
        Film film = filmDbStorage.findById(1).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        filmDbStorage.setLike(film, 1);
        filmDbStorage.setLike(film, 2);
        Assertions.assertEquals(2, filmDbStorage.findById(1).orElseThrow(() -> new NotFoundException("Фильм не найден")).getLikes().size());
        filmDbStorage.unLike(film, 1);
        Assertions.assertEquals(1, filmDbStorage.findById(1).orElseThrow(() -> new NotFoundException("Фильм не найден")).getLikes().size());
    }
}
