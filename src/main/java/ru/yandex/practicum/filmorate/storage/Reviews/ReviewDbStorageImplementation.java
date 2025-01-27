package ru.yandex.practicum.filmorate.storage.Reviews;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

@Repository
public class ReviewDbStorageImplementation extends BaseStorage<Review> implements ReviewDbStorage {
    public ReviewDbStorageImplementation(JdbcTemplate jdbc) {
        super(jdbc);
    }
}
