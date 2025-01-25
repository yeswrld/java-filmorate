package ru.yandex.practicum.filmorate.storage.Likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class LikesDbStorageImplementation implements LikesDbStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Integer> getUsersLikes(Integer id) {
        String getUsersLikes = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        return jdbc.queryForList(getUsersLikes, Integer.class, id);
    }
}
