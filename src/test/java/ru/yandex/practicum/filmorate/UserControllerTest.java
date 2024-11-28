package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
class UserControllerTest {
    UserController userController = new UserController();


    @DisplayName("Добавление пользователя с верными значениями полей")
    @Test
    void createUser() {
        User user = new User("user@email.com", "Login", "Имя", LocalDate.of(1990, 9, 29));
        userController.create(user);
        Assertions.assertEquals(1, userController.findAll().size());
    }

    @DisplayName("Добавление пользователя с емайл без @")
    @Test
    void createUserWtihoutDog() {
        User user = new User("useremail.com", "Login", "Имя"
                , LocalDate.of(1990, 9, 29));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user)
                , "Исключение, пользователь не прошел валидацию");
    }

    @DisplayName("Добавление пользователя с логином содержащим пробелы")
    @Test
    void createUserWithIncorrectLogin() {
        User user = new User("user@email.com", "Log in", "Имя пользователя"
                , LocalDate.of(1990, 9, 29));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user)
                , "Исключение, пользователь не прошел валидацию");
    }

    @DisplayName("Добавление пользователя с пустым именем.(вместо имени  должен появится логин")
    @Test
    void createFilmWithoutName() {
        User user = new User("user@email.com", "Login", ""
                , LocalDate.of(1990, 9, 29));
        userController.create(user);
        Assertions.assertEquals("Login", userController.findAll().stream().toList().getFirst().getName());
    }

    @DisplayName("Добавление пользователя с датой рождения из будущего")
    @Test
    void createFilmNegativeDuration() {
        User user = new User("user@email.com", "Login", "Имя", LocalDate.of(2030, 9, 29));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user)
                , "Исключение, пользователь не прошел валидацию");
    }
}
