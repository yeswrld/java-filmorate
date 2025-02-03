package ru.yandex.practicum.filmorate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(FilmController.class)
public class FilmSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Test
    public void testSearchFilmsByDirectorAndTitle() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Крадущийся тигр, затаившийся дракон");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("PG-13");
        film.setMpa(mpa);

        Director director = Director.builder().id(1).name("Крадущийся режиссер").build();
        film.setDirectors(List.of(director));

        List<Film> films = new ArrayList<>();
        films.add(film);

        when(filmService.searchFilms("крад", "director,title")).thenReturn(films);

        mockMvc.perform(get("/films/search")
                        .param("query", "крад")
                        .param("by", "director,title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Крадущийся тигр, затаившийся дракон"))
                .andExpect(jsonPath("$[0].mpa.name").value("PG-13"))
                .andExpect(jsonPath("$[0].directors[0].name").value("Крадущийся режиссер"));

        verify(filmService, times(1)).searchFilms("крад", "director,title");
    }
}
