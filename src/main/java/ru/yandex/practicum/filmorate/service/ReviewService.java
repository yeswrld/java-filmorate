package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.Reviews.ReviewDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final UserService userService;
    private final FilmService filmService;

    public Review add(Review review) {
        validateReview(review);
        reviewDbStorage.add(review);
        return review;
    }

    public Review update(Review review) {
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
        if (review.getFilmId() == null){
            throw new ValidationException("Поле FilmID пустое");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Поле IsPositive пустое");
        }
        return review;
    }
}
