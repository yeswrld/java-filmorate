package ru.yandex.practicum.filmorate.storage.Reviews;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewDbStorageImplementation extends BaseStorage<Review> implements ReviewDbStorage {
    private final ReviewRowMapper reviewRowMapper;
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorageImplementation(JdbcTemplate jdbc, ReviewRowMapper reviewRowMapper, JdbcTemplate jdbcTemplate) {
        super(jdbc);
        this.reviewRowMapper = reviewRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review add(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("Reviews")
                .usingGeneratedKeyColumns("reviewId");
        Map<String, Object> param = new HashMap<>();
        param.put("content", review.getContent());
        param.put("isPositive", review.getIsPositive());
        param.put("userid", review.getUserId());
        param.put("filmId", review.getFilmId());
        param.put("useful", review.getUseful());
        Number reviewId = simpleJdbcInsert.executeAndReturnKey(param);
        review.setReviewId(reviewId.intValue());
        return review;
    }

    @Override
    public Review update(Review updReview) {
        String updQ = """
                UPDATE REVIEWS SET CONTENT = ?, ISPOSITIVE = ? WHERE REVIEWID = ?
                """;
        jdbc.update(updQ,
                updReview.getContent()
                , updReview.getIsPositive()
                , updReview.getReviewId());
        return updReview;
    }

    @Override
    public void delete(Integer id) {
        String delQ = """
                DELETE FROM REVIEWS WHERE REVIEWID = ?
                """;
        delete(delQ, id);
    }

    @Override
    public Optional<Review> findById(Integer id) {
        String getByIdQ = """
                SELECT * FROM REVIEWS WHERE reviewID = ?
                """;
        try {
            Review result = findOne(reviewRowMapper, getByIdQ, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Review> findAll(Integer filmId, Integer count) {
        String findAllwithoutFilmQ = """
                SELECT * FROM REVIEWS
                ORDER BY useful DESC, REVIEWID
                LIMIT ?
                """;
        String findAllwithFilm = """
                SELECT * FROM REVIEWS
                WHERE FILMID = ?
                ORDER BY useful DESC, REVIEWID
                LIMIT ?
                """;
        if (filmId == null) {
            return findMany(reviewRowMapper, findAllwithoutFilmQ, count);
        } else return findMany(reviewRowMapper, findAllwithFilm, filmId, count);
    }

    @Override
    public void setUseful(Integer reviewId, Integer userId, Integer useful) {
        String usefulQ = """
                UPDATE REVIEWS SET USEFUL = USEFUL + ? where reviewId = ?
                """;
        jdbcTemplate.update(usefulQ, useful, reviewId);
    }


}
