package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event.Event;
import ru.yandex.practicum.filmorate.model.Event.EventOperation;
import ru.yandex.practicum.filmorate.model.Event.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Events.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.Users.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userDbStorage;
    private final EventDbStorage eventDbStorage;

    public Collection<User> findAll() {
        return userDbStorage.findAll();
    }

    public User addUser(User user) {
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

        userDbStorage.addUser(user);
        return user;
    }

    public User updateUser(User newUser) {
        User oldUser = userInDbExist(newUser.getId());
        if (newUser.getEmail().isBlank()) {
            log.error("Поле email пустое");
            throw new ValidationException("Поле email пустое");
        } else if (!newUser.getEmail().contains("@")) {
            log.error("Поле email не содержит символа @");
            throw new ValidationException("Поле email не содержит символа @");
        }
        if (newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.info("В качестве имени присвоен логин, т.к. поле имени было пустым");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        userDbStorage.updateUser(newUser);
        return newUser;
    }


    public User findUserById(int id) {
        return userDbStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public void deleteUserById(int id) {
        userInDbExist(id);
        userDbStorage.deleteUser(id);
    }

    public Set<User> getFriends(Integer id) {
        userInDbExist(id);
        return Set.copyOf(userDbStorage.getFriends(id));
    }

    public void addFriend(Integer userA, Integer userB) {
        if (userInDbExist(userA) != null && userInDbExist(userB) != null) {
            userDbStorage.addFriend(userA, userB);
            eventDbStorage.add(EventType.FRIEND, EventOperation.ADD, userA, userB);
        }
    }

    public void deleteFriend(Integer userA, Integer userB) {
        if (userInDbExist(userA) != null && userInDbExist(userB) != null) {
            userDbStorage.deleteFriend(userA, userB);
            eventDbStorage.add(EventType.FRIEND, EventOperation.REMOVE, userA, userB);
        }
    }

    public Set<User> getCommonFriends(Integer userA, Integer userB) {
        log.info("Ищем общих друзей пользователя с ИД = {} и ИД = {}", userA, userB);
        userInDbExist(userA);
        userInDbExist(userB);
        return new HashSet<>(userDbStorage.getCommonFriends(userA, userB));
    }

    public Collection<Event> getFeed(Integer userId) {
        userInDbExist(userId);
        return eventDbStorage.getAll(userId);
    }

    public User userInDbExist(Integer id) {
        Optional<User> user = userDbStorage.findUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.orElse(null);
    }


}
