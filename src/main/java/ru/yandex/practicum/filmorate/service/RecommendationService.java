package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Likes.LikesDbStorage;

import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final LikesDbStorage likesDbStorage;
    private final UserService userService;
    private final FilmService filmService;

    public List<Film> getRecommendations(int userId) {
        userService.userInDbExist(userId);

        Set<Integer> currentUserLikes = likesDbStorage.getFilmsLikedByUser(userId);
        if (currentUserLikes.isEmpty()) {
            log.info("У пользователя с id={} нет лайков, возвращаем пустой список рекомендаций", userId);
            return Collections.emptyList();
        }

        int maxIntersection = 0;
        Integer similarUserId = null;

        for (User other : userService.findAll()) {
            if (!Objects.equals(other.getId(), userId)) {
                Set<Integer> otherUserLikes = likesDbStorage.getFilmsLikedByUser(other.getId());
                Set<Integer> intersection = new HashSet<>(currentUserLikes);
                intersection.retainAll(otherUserLikes);
                int commonLikesCount = intersection.size();

                if (commonLikesCount > maxIntersection) {
                    maxIntersection = commonLikesCount;
                    similarUserId = other.getId();
                }
            }
        }

        if (similarUserId == null || maxIntersection == 0) {
            log.info("Не найдено ни одного пользователя с пересечением лайков для userId={}", userId);
            return Collections.emptyList();
        }

        Set<Integer> similarUserLikes = likesDbStorage.getFilmsLikedByUser(similarUserId);
        similarUserLikes.removeAll(currentUserLikes);

        if (similarUserLikes.isEmpty()) {
            log.info("У похожего пользователя нет новых фильмов, которых бы не видел userId={}", userId);
            return Collections.emptyList();
        }

        List<Film> recommended = new ArrayList<>();
        for (Integer filmId : similarUserLikes) {
            recommended.add(filmService.findById(filmId));
        }

        log.info("Для пользователя id={} сформирован список из {} рекомендаций", userId, recommended.size());
        return recommended;
    }
}
