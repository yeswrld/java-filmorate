package ru.yandex.practicum.filmorate.storage.Users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDbStorage {
    User addUser(User user);

    User updateUser(User newUser);

    void deleteUser(Integer id);

    Optional<User> findUserById(int id);

    Collection<User> findAll();

    void addFriend(Integer userA, Integer userB);

    Collection<Integer> getFriends(Integer id);

    void deleteFriend(Integer userA, Integer userB);

}
