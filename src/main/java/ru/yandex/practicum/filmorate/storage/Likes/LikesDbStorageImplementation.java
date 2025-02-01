package ru.yandex.practicum.filmorate.storage.Likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikesDbStorageImplementation implements LikesDbStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Integer> getUsersLikes(Integer id) {
        String getUsersLikes = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        return jdbc.queryForList(getUsersLikes, Integer.class, id);
    }

    @Override
    public Set<Integer> getFilmsLikedByUser(Integer userId) {
        String sql = "SELECT FILM_ID FROM LIKES WHERE USER_ID = ?";
        return new HashSet<>(jdbc.queryForList(sql, Integer.class, userId));
    }
}
