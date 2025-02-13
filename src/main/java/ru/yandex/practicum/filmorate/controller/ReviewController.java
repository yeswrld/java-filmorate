package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.LikeDislike;
import ru.yandex.practicum.filmorate.model.LikeForReview;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@RequestBody Review review) throws NotFoundException {
        log.info("Добавляем отзыв с ИД = {}", review.getReviewId());
        reviewService.add(review);
        return review;
    }

    @PutMapping
    public Review update(@RequestBody Review updReview) {
        log.info("Обновляем отзыв с ИД = {}", updReview.getReviewId());
        reviewService.update(updReview);
        return updReview;
    }

    @DeleteMapping("/{id}")
    public Review delete(@PathVariable Integer id) {
        log.info("Удаляем отзыв с ИД = {}", id);
        return reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Integer id) {
        log.info("Выводим отзыв с ИД = {}", id);
        return reviewService.getById(id);
    }

    @GetMapping
    public Collection<Review> getReviews(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue = "10") Integer count) {
        log.info("Выводим отзывы к фильму c ИД = {} в количестве {}", filmId, count);
        return reviewService.getAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public LikeForReview addLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Ставим лайк к отзыву с ИД = {} от пользователя с ИД = {}", reviewId, userId);
        return reviewService.like(reviewId, userId, LikeDislike.LIKE);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public LikeForReview addDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Ставим дизлайк к отзыву с ИД = {} от пользователя с ИД = {}", reviewId, userId);
        return reviewService.like(reviewId, userId, LikeDislike.DISLIKE);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Удаляем лайк у ревью с ИД = {} от пользователя с ИД = {} ", reviewId, userId);
        reviewService.deleteLike(reviewId, userId, LikeDislike.LIKE);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deletedislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Удаляем дизлайк у ревью с ИД = {} от пользователя с ИД = {} ", reviewId, userId);
        reviewService.deleteLike(reviewId, userId, LikeDislike.DISLIKE);
    }

}
