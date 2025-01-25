package ru.yandex.practicum.filmorate.storage.Mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDbStorage {
    Collection<Mpa> findAll();

    Mpa get(Integer id);

    Mpa findById(Integer id);

    boolean mpaExists(Integer id);
}
