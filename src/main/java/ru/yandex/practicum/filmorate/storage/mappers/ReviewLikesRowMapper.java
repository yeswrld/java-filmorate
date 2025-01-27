package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.LikeDislike;
import ru.yandex.practicum.filmorate.model.LikeForReview;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewLikesRowMapper implements RowMapper<LikeForReview> {
    @Override
    public LikeForReview mapRow(ResultSet rs, int rowNum) throws SQLException {
        LikeForReview likeForReview = new LikeForReview();
        likeForReview.setReviewId(rs.getInt("reviewId"));
        likeForReview.setUserId(rs.getInt("userId"));
        likeForReview.setLikeType(LikeDislike.valueOf(rs.getString("type")));
        return likeForReview;
    }
}
