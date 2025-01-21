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
        log.info("Возвращаем список пользователей, всго найдено - {}", userDbStorage.findAll().size());
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
        log.info("Ищем пользователя с ИД = {}", id);
        return userDbStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public void deleteUserById(int id) {
        log.info("Удаляем пользователя с ИД = {}", id);
        userInDbExist(id);
        userDbStorage.deleteUser(id);
        log.info("Пользователь с ИД = {} удалён", id);
    }

    public Set<User> getFriends(Integer id) {
        log.info("Ищем друзей пользователя с ИД = {}", id);
        userInDbExist(id);
        log.info("Ищем друзей пользователя {}", id);
        Collection<Integer> friendsIds = userDbStorage.getFriends(id);
        Set<User> friends = new HashSet<>();
        for (Integer i : friendsIds) {
            friends.add(userDbStorage.findUserById(i).orElse(null));
        }
        log.info("У пользователя с ИД = {} друзья {}", id, friends);
        return friends;
    }

    public void addFriend(Integer userA, Integer userB) {
        log.info("Добавляем к пользователю с ИД =  {} в друзья пользователя с ИД =  {}", userA, userB);
        if (userInDbExist(userA) != null && userInDbExist(userB) != null) {
            userDbStorage.addFriend(userA, userB);
        }
        log.info("{} и {} теперь друзья ", userA, userB);
    }

    public void deleteFriend(Integer userA, Integer userB) {
        log.info("Удаляем у пользователя с ИД = {} друга-пользователя с ИД = {}", userA, userB);
        if (userInDbExist(userA) != null && userInDbExist(userB) != null) {
            userDbStorage.deleteFriend(userA, userB);
        }
        log.info("Пользователь с ИД =  {} и с ИД = {} больше не друзья", userA, userB);
    }

    public Set<User> getCommonFriends(Integer userA, Integer userB) {
        log.info("Ищем общих друзей пользователя с ИД = {} и ИД = {}", userA, userB);
        userInDbExist(userA);
        userInDbExist(userB);
        Set<Integer> userAfriends = new HashSet<>(userDbStorage.getFriends(userA));
        Set<Integer> userBfriends = new HashSet<>(userDbStorage.getFriends(userB));
        userAfriends.retainAll(userBfriends);
        Set<User> commonFriends = new HashSet<>();
        for (Integer i : userAfriends) {
            commonFriends.add(userDbStorage.findUserById(i).orElse(null));
        }
        log.info("Общие друзья - {}", commonFriends);
        return commonFriends;
    }

    public User userInDbExist(Integer id) {
        Optional<User> user = userDbStorage.findUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.orElse(null);
    }


}
