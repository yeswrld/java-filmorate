package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Data
@EqualsAndHashCode
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> likes;
    private Mpa mpa;
    private List<Genre> genres;


}

