package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserHaveLike;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.LikeDislike;
import ru.yandex.practicum.filmorate.model.LikeForReview;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Reviews.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.Reviews.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.Users.UserDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final UserDbStorage userDbStorage;

    public Review add(Review review) {
        validateReview(review);
        reviewDbStorage.add(review);
        return review;
    }

    public Review update(Review review) {
        validateReview(review);
        reviewDbStorage.findById(review.getReviewId()).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        return reviewDbStorage.update(review);
    }

    public Review getById(Integer id) {
        return reviewDbStorage.findById(id).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    public Review delete(Integer id) {
        Review deletedReview = reviewDbStorage.findById(id).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        reviewDbStorage.delete(id);
        return deletedReview;
    }

    public Collection<Review> getAll(Integer filmId, Integer count) {
        return reviewDbStorage.findAll(filmId, count);
    }

    public Review validateReview(Review review) {
        if (review.getContent() == null) {
            throw new ValidationException("Поле контент пустое");
        }
        if (review.getUserId() < 0) {
            throw new NotFoundException("Поле UserID меньше нуля");
        }
        if (review.getUserId() == null) {
            throw new ValidationException("Поле UserID пустое");
        }
        if (review.getFilmId() < 0) {
            throw new NotFoundException("Поле FilmID меньше нуля");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("Поле FilmID пустое");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Поле IsPositive пустое");
        }
        if (review.getUseful() == null) {
            review.setUseful(0);
        }
        return review;
    }


    public LikeForReview like(Integer reviewId, Integer userId, LikeDislike type) {
        Review review = reviewDbStorage.findById(reviewId).orElseThrow(() -> new NotFoundException("Ревью не найдено"));
        User user = userDbStorage.findUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LikeForReview like = likeDbStorage.getLike(review.getReviewId(), user.getId());
        if (like != null && type.equals(like.getLikeType())) {
            throw new UserHaveLike("Пользователь уже поставил лайк/дизлайк к этому отзыву");
        } else if (like != null && !type.equals(like.getLikeType())) {
            likeDbStorage.deleteLike(reviewId, userId, like.getLikeType());
        }
        likeDbStorage.like(reviewId, userId, type);
        LikeForReview newLike = likeDbStorage.getLike(reviewId, userId);

        Integer useful = 0;
        if (type.equals(LikeDislike.LIKE)) {
            useful = 1;
        }
        if (type.equals(LikeDislike.DISLIKE)) {
            useful = -1;
        }
        reviewDbStorage.setUseful(reviewId, userId, useful);
        return newLike;
    }

    public LikeForReview getLike(Integer reviewId, Integer userId) {
        return likeDbStorage.getLike(reviewId, userId);
    }


    public void deleteLike(Integer reviewId, Integer userId, LikeDislike type) {
        User user = userDbStorage.findUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Review review = reviewDbStorage.findById(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        likeDbStorage.deleteLike(reviewId, user.getId(), type);
    }
}
