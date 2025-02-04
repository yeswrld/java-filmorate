package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Directors.DirectorDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    public Director findById(Integer id) {
        if (!directorDbStorage.directorsExist(id)) {
            log.info("Режиссер не найден");
            throw new NotFoundException("Режиссер не найден");
        } else {
            return directorDbStorage.findById(id);
        }
    }

    public Collection<Director> findAll() {
        return directorDbStorage.findAll();
    }

    public Director create(Director director) {
        validateDirector(director);
        return directorDbStorage.create(director);
    }

    public Director update(Director newDirector) {
        if (!directorDbStorage.directorsExist(newDirector.getId())) {
            log.info("Нельзя обновить: Режиссер не найден");
            throw new NotFoundException("Режиссер не найден");
        } else {
            return directorDbStorage.update(newDirector);
        }
    }

    public void removeById(Integer id) {
        if (!directorDbStorage.directorsExist(id)) {
            log.info("Нельзя удалить: Режиссер не найден");
            throw new NotFoundException("Режиссер не найден");
        } else {
            directorDbStorage.removeById(id);
        }
    }

    private void validateDirector(Director director) {
        if(director.getName() == null || director.getName().isBlank()) {
            log.info("Нельзя создать: Имя режиссера не заданно");
            throw new ValidationException("Нельзя создать: Имя режиссера не заданно");
        }
    }
}
