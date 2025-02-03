package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Выводим всех пользователей с базы");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUserByID(@PathVariable Integer id) {
        log.info("Выводим пользователя с ИД = {}", id);
        return userService.findUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Set<User> findUserFriends(@PathVariable Integer id) {
        log.info("Получен запрос друзей пользователя {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{anotherId}")
    public Set<User> commonFriends(@PathVariable Integer id, @PathVariable Integer anotherId) {
        log.info("Выводим общих друзей пользователей с ИД = {} и ИД ={}", id, anotherId);
        return userService.getCommonFriends(id, anotherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) throws NotFoundException {
        log.info("Добавляем пользователя {}", user);
        userService.addUser(user);
        log.info("Пользователь успешно добавлен");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Обновляем пользователя {}", user);
        userService.updateUser(user);
        log.info("Пользователь успешно обновлен");
        return user;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Удаляем пользователя с ИД = {}", id);
        userService.deleteUserById(id);
        log.info("Пользователь успешно удален");
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Добавляем в друзья к пользователю c ИД = {} пользователя с ИД = {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователи успешно подружились");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Удаляем из друзей пользователя с ИД = {} пользователя с ИД = {}", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Пользователи больше не друзья");
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Integer id) {
        log.info("Получен запрос рекомендаций для пользователя с ИД = {}", id);
        return recommendationService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> getFeed(@PathVariable Integer id) {
        log.info("Получен запрос ленту событий для пользователя с ИД = {}", id);
        return userService.getFeed(id);
    }

}
