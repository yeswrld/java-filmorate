package ru.yandex.practicum.filmorate.storage.Users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UserDbStorageImplemetation extends BaseStorage<User> implements UserDbStorage {
    private final UserRowMapper userRowMapper;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorageImplemetation(JdbcTemplate jdbc, UserRowMapper userRowMapper, JdbcTemplate jdbcTemplate) {
        super(jdbc);
        this.userRowMapper = userRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("ID");
        Map<String, Object> param = new HashMap<>();
        param.put("EMAIL", user.getEmail());
        param.put("LOGIN", user.getLogin());
        param.put("NAME", user.getName());
        param.put("BIRTHDAY", user.getBirthday());
        Number userId = simpleJdbcInsert.executeAndReturnKey(param);
        user.setId(userId.intValue());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        String updQ = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?";
        jdbc.update(updQ,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public Optional<User> findById(int id) {
        String findByIdQ = "SELECT * FROM USERS WHERE ID = ?";
        try {
            User result = findOne(userRowMapper, findByIdQ, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> findAll() {
        String findAllQ = "SELECT * FROM USERS";
        return findMany(userRowMapper, findAllQ);
    }

    @Override
    public User findUserById(int id) {
        return null;
    }

    @Override
    public void addFriend(Integer userA, Integer userB) {
        String addFriendQ = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        update(addFriendQ, userA, userB);
    }

    @Override
    public void deleteFriend(Integer userA, Integer userB) {
        String deleteFriendQ = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        delete(deleteFriendQ, userA, userB);
    }

    @Override
    public Collection<Integer> getFriends(Integer id) {
        String getFriendsQ = "SELECT ID FROM USERS WHERE ID = ? ";
        jdbcTemplate.queryForObject(getFriendsQ, Integer.class, id);
        String result = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
        return jdbcTemplate.queryForList(result, Integer.class, id);
    }

}
