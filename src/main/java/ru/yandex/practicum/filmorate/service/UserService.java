package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User addOrUpdateUser(User user) {
        return userStorage.addOrUpdateUser(user);
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id);
    }

    public Collection<User> getFriends(Integer id) {
        return userStorage.getFriends(id);
    }

    public User addFriend(Integer userA, Integer userB) {
        return userStorage.addFriend(userA, userB);
    }

    public void deleteFriend(Integer userA, Integer userB) {
        userStorage.deleteFriend(userA, userB);
    }

    public Collection<User> getCommonFriends(Integer id, Integer anotherId) {
        return userStorage.getCommonFriends(id, anotherId);
    }
}
