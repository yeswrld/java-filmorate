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
        log.info("Получен запрос на МРА с ИД = {}", id);
        if (!mpaDbStorage.mpaExists(id)) {
            log.warn("МРА с ИД={} не найден", id);
            throw new NotFoundException("МРА с запрашиваемым ид не найден");
        }
        log.info("МРА с ИД = {} - это {}", id, mpaDbStorage.get(id).getName());
        return mpaDbStorage.findById(id);
    }

    public boolean mpaExists(Integer id) {
        log.info("Проверяем есть ли МРА с ИД = {} в базе", id);
        return mpaDbStorage.mpaExists(id);
    }
}
