package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LikeForReview {
    private Integer reviewId;
    private Integer userId;
    private LikeDislike likeType;
}
