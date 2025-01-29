package ru.yandex.practicum.filmorate.storage.Reviews;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.LikeDislike;
import ru.yandex.practicum.filmorate.model.LikeForReview;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

@Repository
public class LikeDbStorageImplementation extends BaseStorage<LikeForReview> implements LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<LikeForReview> reviewRowMapper;

    public LikeDbStorageImplementation(JdbcTemplate jdbc, JdbcTemplate jdbcTemplate, RowMapper<LikeForReview> reviewRowMapper) {
        super(jdbc);
        this.jdbcTemplate = jdbcTemplate;
        this.reviewRowMapper = reviewRowMapper;
    }


    @Override
    public LikeForReview like(Integer reviewId, Integer userId, LikeDislike type) {
/*        String addLikeDislakeQ = String.format( """
                INSERT INTO review_likes VALUES (?, ?, '%s')
                """, type);*/
        String addLikeDislakeQ = String.format("INSERT INTO review_likes VALUES(?,?,'%s')", type);
        update(addLikeDislakeQ, reviewId, userId);
        LikeForReview likeForReview = new LikeForReview();
        likeForReview.setReviewId(reviewId);
        likeForReview.setUserId(userId);
        likeForReview.setLikeType(type);
  /*      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("review_likes")
                .usingGeneratedKeyColumns("reviewId");
        Map<String, Object> param = new HashMap<>();
        param.put("userId", likeForReview.getUserId());
        param.put("type", likeForReview.getLikeType());
        Number reviewId = simpleJdbcInsert.executeAndReturnKey(param);
        likeForReview.setReviewId(reviewId.intValue());
        return likeForReview;*/
        return likeForReview;
    }

    @Override
    public LikeForReview getLike(Integer reviewId, Integer userId) {
        String getLike = """
                SELECT * FROM review_likes WHERE reviewId = ? AND userId = ?
                """;

        return findOne(reviewRowMapper, getLike, reviewId, userId);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId, LikeDislike likeType) {
        String deleteLike = """
                DELETE FROM review_likes WHERE reviewId = ? AND userId = ? AND type = ?
                """;
        delete(deleteLike, reviewId, userId, likeType.name());
    }
}
