package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;
import java.util.List;

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
        return reviewService.getAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {

    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {

    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deletedislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {

    }

}
