package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public Collection<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    public Mpa findById(Integer id) {
        if (!mpaDbStorage.mpaExists(id)) {
            log.info("МРА с запрашиваемым ид не найден");
            throw new NotFoundException("МРА с запрашиваемым ид не найден");
        }
        return mpaDbStorage.findById(id);
    }

    public boolean mpaExists(Integer id) {
        return mpaDbStorage.mpaExists(id);
    }
}
