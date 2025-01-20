package ru.yandex.practicum.filmorate.storage.Users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserDbStorage {
    User addUser(User user);

    User updateUser(User newUser);

    Optional<User> findById(int id);

    Collection<User> findAll();

    User findUserById(int id);

    void addFriend(Integer userA, Integer userB);

    Collection<Integer> getFriends(Integer id);

    void deleteFriend(Integer userA, Integer userB);

}
