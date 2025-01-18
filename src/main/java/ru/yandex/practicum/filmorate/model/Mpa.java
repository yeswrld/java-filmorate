package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder(toBuilder = true)
public class Mpa {
    private Integer id;
    private String name;
}
