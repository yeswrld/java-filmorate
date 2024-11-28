package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) throws NotFoundException {
        if (user.getEmail().isBlank()) {
            log.error("Поле email пустое");
            throw new ValidationException("Поле email пустое");
        } else if (!user.getEmail().contains("@")) {
            log.error("Поле email не содержит символа @");
            throw new ValidationException("Поле email не содержит символа @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("В качестве имени присвоен логин, т.к. поле имени было пустым");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с именем " + user.getName() + " и ID = " + user.getId() + " добавлен");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("ID не указан");
            throw new ValidationException("ID не указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail().isBlank()) {
                log.error("Поле email пустое");
                throw new ValidationException("Поле email пустое");
            } else if (!newUser.getEmail().contains("@")) {
                log.error("Поле email не содержит символа @");
                throw new ValidationException("Поле email не содержит символа @");
            } else if (newUser.getLogin().isEmpty()) {
                log.error("Логин не может быть пустым и содержать пробелы");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            } else if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
                log.info("В качестве имени присвоен логин, т.к. поле имени было пустым");
            } else if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.error("Дата рождения не может быть в будущем");
                throw new ValidationException("В качестве имени присвоен логин, т.к. поле имени было пустым");
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь с именем " + oldUser.getName() + " и ID = " + oldUser.getId() + " обновлен");
            return oldUser;
        }
        log.error("Пользователь с ID = " + newUser.getId() + " не найден");
        throw new NotFoundException();
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;

    }
}
