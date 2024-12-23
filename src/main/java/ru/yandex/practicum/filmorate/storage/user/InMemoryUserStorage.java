package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User addOrUpdateUser(User user) {
        if (user.getId() == null) {
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
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getEmail().isBlank()) {
                log.error("Поле email пустое");
                throw new ValidationException("Поле email пустое");
            } else if (!user.getEmail().contains("@")) {
                log.error("Поле email не содержит символа @");
                throw new ValidationException("Поле email не содержит символа @");
            } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
                log.error("Логин не может быть пустым и содержать пробелы");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            } else if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.info("В качестве имени присвоен логин, т.к. поле имени было пустым");
            } else if (user.getBirthday().isAfter(LocalDate.now())) {
                log.error("Дата рождения не может быть в будущем");
                throw new ValidationException("В качестве имени присвоен логин, т.к. поле имени было пустым");
            }
            oldUser.setEmail(user.getEmail());
            oldUser.setLogin(user.getLogin());
            oldUser.setName(user.getName());
            oldUser.setBirthday(user.getBirthday());
            log.info("Пользователь с именем " + oldUser.getName() + " и ID = " + oldUser.getId() + " обновлен");
            return oldUser;
        } else {
            log.error("Пользователь с ID = " + user.getId() + " не найден");
            throw new NotFoundException("Пользователь с указанным ИД не найден");
        }
    }

    @Override
    public User findUserById(int id) {
        Optional<User> userOptional = Optional.ofNullable(users.get(id));
        if (userOptional.isEmpty()) throw new NotFoundException("Пользователь не найден");
        return userOptional.get();
    }

    @Override
    public Collection<User> getFriends(Integer id) {
        User userFromStorage = findUserById(id);
        log.info("Получен запрос друзей пользователя " + userFromStorage.getName());
        if (userFromStorage.getFriends() == null) {
            return new HashSet<>();
        }
        return userFromStorage.getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public User addFriend(Integer userA, Integer userB) {
        User userFromUStorage = findUserById(userA);
        User friendFromUStorage = findUserById(userB);
        if (userA.equals(userB)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
        updateUserFriendsSet(userFromUStorage, userB);
        updateUserFriendsSet(friendFromUStorage, userA);
        log.info("Пользователь " + userFromUStorage.getName() + " и пользователь " + friendFromUStorage.getName() + " теперь друзья");
        return userFromUStorage;
    }

    @Override
    public void deleteFriend(Integer userA, Integer userB) {
        User userFromUStorage = findUserById(userA);
        User friendFromUStorage = findUserById(userB);
        deleteFriendFromUserSet(userFromUStorage, userB);
        deleteFriendFromUserSet(friendFromUStorage, userA);
        log.info("Пользователь " + userFromUStorage.getName() + " удалил друга " + friendFromUStorage.getName());
    }

    @Override
    public Collection<User> getCommonFriends(Integer id, Integer anotherId) {
        User firstUserFromUStorage = findUserById(id);
        User nextUserFromUStorage = findUserById(anotherId);
        log.info("Получен запрос общих друзей пользователей " + firstUserFromUStorage.getName() + " и" + nextUserFromUStorage.getName());
        if (id.equals(anotherId)) {
            throw new ValidationException("ID пользователей совпадают");
        }
        Set<Integer> commonFriends = new HashSet<>(firstUserFromUStorage.getFriends());
        commonFriends.retainAll(nextUserFromUStorage.getFriends());
        return commonFriends.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    private void updateUserFriendsSet(User user, Integer id) {
        Set<Integer> friends = new HashSet<>();
        if (user.getFriends() != null) {
            friends = user.getFriends();
        }
        friends.add(id);
        user.setFriends(friends);
    }

    private void deleteFriendFromUserSet(User user, Integer id) {
        Set<Integer> friends = new HashSet<>();
        if (user.getFriends() != null) {
            friends = user.getFriends();
            friends.remove(id);
        }
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
