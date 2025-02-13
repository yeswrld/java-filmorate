package ru.yandex.practicum.filmorate.storage.Likes;

import java.util.Collection;
import java.util.Set;

public interface LikesDbStorage {
    Collection<Integer> getUsersLikes(Integer id);

    Set<Integer> getFilmsLikedByUser(Integer userId);
}
