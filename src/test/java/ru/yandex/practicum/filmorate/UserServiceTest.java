package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Users.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserServiceTest {
    private final UserDbStorage userDbStorage;

    @Test
    public void findAll() {
        assertThat(userDbStorage.findAll()).hasSize(2);
    }

    @Test
    public void findUserById() {
        User user = new User();
        user.setId(1);
        user.setEmail("yane@sobaka.ru");
        user.setLogin("login");
        user.setName("cat");
        user.setBirthday(LocalDate.of(1990, 10, 15));
        User userFromDb = userDbStorage.findUserById(1).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        System.out.println(userFromDb.toString());
        Assertions.assertEquals(user, userFromDb);
    }

    @Test
    public void updateUser() {
        User userFromDb = userDbStorage.findUserById(1).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userFromDb.setName("Jacky Chan");
        userFromDb.setEmail("ya@sobaka.ru");
        userDbStorage.updateUser(userFromDb);
        assertThat(userFromDb)
                .hasFieldOrPropertyWithValue("email", "ya@sobaka.ru")
                .hasFieldOrPropertyWithValue("name", "Jacky Chan");

    }

    @Test
    public void deleteUser() {
        userDbStorage.deleteUser(1);
        assertThat(userDbStorage.findAll()).hasSize(1);
    }

    @Test
    public void deleteNotExistUser() {
        userDbStorage.deleteUser(55);
        assertThat(userDbStorage.findAll()).hasSize(2);
    }
}
