package ru.yandex.practicum.filmorate.storage.Reviews;

import ru.yandex.practicum.filmorate.model.LikeDislike;
import ru.yandex.practicum.filmorate.model.LikeForReview;

public interface LikeDbStorage {
    LikeForReview like(Integer reviewId, Integer userId, LikeDislike type);

    LikeForReview getLike(Integer reviewId, Integer userId);

    void deleteLike(Integer reviewId, Integer userId, LikeDislike likeType);
}
