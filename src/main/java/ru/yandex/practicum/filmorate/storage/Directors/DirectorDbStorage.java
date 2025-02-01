package ru.yandex.practicum.filmorate.storage.Directors;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorDbStorage {
   Director findById(Integer id);

    Collection<Director> findAll();

    Director create(Director director);

    Director update(Director newDirector);

    void removeById(Integer id);

    public boolean directorsExist(Integer id);
}
