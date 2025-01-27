package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@RequestBody User user) throws NotFoundException {
        return new Review();
    }

    @PutMapping
    public Review update(@RequestBody User user) {
        return new Review();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Integer id) {
        return new Review();
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue = "10") Integer count) {
        return List.of();
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
