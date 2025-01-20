package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
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

    public Collection<User> findAll() {
        return userDbStorage.findAll();
    }

    public User addUser(User user) {
        log.info("Получен запрос на добавление пользователя {}", user.getName());
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
        log.info("Пользователь с именем {} и ID = {} добавлен", user.getName(), user.getId());
        return user;
    }

    public User updateUser(User newUser) {
        log.info("Получен запрос на обновление пользователя {}", newUser.getId());
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
        log.info("Пользователь с именем {} и ID = {} обновлен", newUser.getName(), newUser.getId());
        return newUser;
    }


    public User findUserById(int id) {
        return userDbStorage.findUserById(id);
    }

    public Set<User> getFriends(Integer id) {
        userInDbExist(id);
        log.info("Ищем друзей пользователя {}", id);
        Collection<Integer> friendsIds = userDbStorage.getFriends(id);
        Set<User> friends = new HashSet<>();
        for (Integer i : friendsIds) {
            friends.add(userDbStorage.findById(i).orElse(null));
        }
        log.info("На выходе - {}", friends.size());
        return friends;
    }

    public void addFriend(Integer userA, Integer userB) {
        log.info("Добавляем к пользователю {} в друзья пользователя {}", userA, userB);
        if (userInDbExist(userA) != null && userInDbExist(userB) != null) {
            userDbStorage.addFriend(userA, userB);
        }
        log.info("{} и {} теперь друзья ", userA, userB);
    }

    public void deleteFriend(Integer userA, Integer userB) {
        if (userInDbExist(userA) != null && userInDbExist(userB) != null) {
            userDbStorage.deleteFriend(userA, userB);
        }
    }

    public Set<User> getCommonFriends(Integer userA, Integer userB) {
        userInDbExist(userA);
        userInDbExist(userB);
        Set<Integer> userAfriends = new HashSet<>(userDbStorage.getFriends(userA));
        Set<Integer> userBfriends = new HashSet<>(userDbStorage.getFriends(userB));
        userAfriends.retainAll(userBfriends);
        Set<User> commonFriends = new HashSet<>();
        for (Integer i : userAfriends) {
            commonFriends.add(userDbStorage.findById(i).orElse(null));
        }
        log.info("COMMON FRIENDS - {}", commonFriends.toString());
        return commonFriends;
    }

    public User userInDbExist(Integer id) {
        Optional<User> user = userDbStorage.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.orElse(null);
    }


}
