package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Data
@Builder(toBuilder = true)
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> likes;
    private Mpa mpa;
    private Collection<Genre> genres;

    //конструктор для тестов
//    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
//        this.name = name;
//        this.description = description;
//        this.releaseDate = releaseDate;
//        this.duration = duration;
//    }
}

