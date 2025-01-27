package ru.yandex.practicum.filmorate.storage.Reviews;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewDbStorage {
    Review add(Review review);

    Review update(Review newReview);

    void delete(Integer id);

    Optional<Review> findById(Integer id);

    Collection<Review> findAll(Integer filmId, Integer count);

    void setUseful(Integer reviewId, Integer userId, Integer useful);
}
