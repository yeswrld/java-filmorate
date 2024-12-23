package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> findById(int id);

    Collection<User> findAll();

    User addOrUpdateUser(User user);

    User findUserById(int id);

    Collection<User> getFriends(Integer id);

    User addFriend(Integer userA, Integer userB);

    void deleteFriend(Integer userA, Integer userB);

    Collection<User> getCommonFriends(Integer id, Integer anotherId);
}
