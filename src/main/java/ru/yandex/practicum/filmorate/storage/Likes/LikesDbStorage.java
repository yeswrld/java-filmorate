package ru.yandex.practicum.filmorate.storage.Likes;

import java.util.Collection;

public interface LikesDbStorage {
    Collection<Integer> getUsersLikes(Integer id);
}
