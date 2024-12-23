package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
class InMemoryUserStorageTest {
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();


    @DisplayName("Добавление пользователя с верными значениями полей")
    @Test
    void createUser() {
        User user = new User("user@email.com", "Login", "Имя", LocalDate.of(1990, 9, 29));
        inMemoryUserStorage.addOrUpdateUser(user);
        Assertions.assertEquals(1, inMemoryUserStorage.findAll().size());
    }

    @DisplayName("Добавление пользователя с емайл без @")
    @Test
    void createUserWtihoutDog() {
        User user = new User("useremail.com", "Login", "Имя", LocalDate.of(1990, 9, 29));
        Assertions.assertThrows(ValidationException.class, () -> inMemoryUserStorage.addOrUpdateUser(user), "Исключение, пользователь не прошел валидацию");
    }

    @DisplayName("Добавление пользователя с логином содержащим пробелы")
    @Test
    void createUserWithIncorrectLogin() {
        User user = new User("user@email.com", "Log in", "Имя пользователя", LocalDate.of(1990, 9, 29));
        Assertions.assertThrows(ValidationException.class, () -> inMemoryUserStorage.addOrUpdateUser(user), "Исключение, пользователь не прошел валидацию");
    }

    @DisplayName("Добавление пользователя с пустым именем.(вместо имени  должен появится логин")
    @Test
    void createUserWithoutName() {
        User user = new User("user@email.com", "Login", "", LocalDate.of(1990, 9, 29));
        inMemoryUserStorage.addOrUpdateUser(user);
        Assertions.assertEquals("Login", inMemoryUserStorage.findAll().stream().toList().getFirst().getName());
    }

    @DisplayName("Добавление пользователя с датой рождения из будущего")
    @Test
    void createUserWithIncorrectBirthday() {
        User user = new User("user@email.com", "Login", "Имя", LocalDate.of(2030, 9, 29));
        Assertions.assertThrows(ValidationException.class, () -> inMemoryUserStorage.addOrUpdateUser(user), "Исключение, пользователь не прошел валидацию");
    }

    @DisplayName("Добавление пользователем друга")
    @Test
    void userAddFriend() {
        User user = new User("user@email.com", "Login", "Степашка", LocalDate.of(1993, 9, 29));
        User user2 = new User("user2@email.com", "Login2", "Адам", LocalDate.of(1995, 9, 29));
        inMemoryUserStorage.addOrUpdateUser(user);
        inMemoryUserStorage.addOrUpdateUser(user2);
        inMemoryUserStorage.addFriend(1, 2);
        Assertions.assertEquals(2, 2, "Друг пользователя " + user.getName() + " не соответствует ожидаемому");
    }

    @DisplayName("Получение друзей опр. пользователя")
    @Test
    void userGetFriend() {
        User user = new User("user@email.com", "Login", "Степашка", LocalDate.of(1993, 9, 29));
        User user2 = new User("user2@email.com", "Login2", "Адам", LocalDate.of(1995, 9, 29));
        User user3 = new User("user3@email.com", "Login3", "Ева", LocalDate.of(1995, 2, 15));
        User user4 = new User("user4@email.com", "Login4", "Джон", LocalDate.of(1995, 2, 25));
        inMemoryUserStorage.addOrUpdateUser(user);
        inMemoryUserStorage.addOrUpdateUser(user2);
        inMemoryUserStorage.addOrUpdateUser(user3);
        inMemoryUserStorage.addOrUpdateUser(user4);
        inMemoryUserStorage.addFriend(1, 2);
        inMemoryUserStorage.addFriend(1, 3);
        inMemoryUserStorage.addFriend(1, 4);
        Set<Integer> setOfExpectedFriends = Set.of(2, 3, 4);
        Assertions.assertEquals(setOfExpectedFriends, user.getFriends(), "Друзья пользователя " + user.getName() + " не соответствуют ожидаемому");
    }

    @DisplayName("Удаление друга у опр. пользователя")
    @Test
    void userDelFriend() {
        User user = new User("user@email.com", "Login", "Степашка", LocalDate.of(1993, 9, 29));
        User user2 = new User("user2@email.com", "Login2", "Адам", LocalDate.of(1995, 9, 29));
        User user3 = new User("user3@email.com", "Login3", "Ева", LocalDate.of(1995, 2, 15));
        User user4 = new User("user4@email.com", "Login4", "Джон", LocalDate.of(1995, 2, 25));
        inMemoryUserStorage.addOrUpdateUser(user);
        inMemoryUserStorage.addOrUpdateUser(user2);
        inMemoryUserStorage.addOrUpdateUser(user3);
        inMemoryUserStorage.addOrUpdateUser(user4);
        inMemoryUserStorage.addFriend(1, 2);
        inMemoryUserStorage.addFriend(1, 3);
        inMemoryUserStorage.addFriend(1, 4);
        inMemoryUserStorage.deleteFriend(1, 3);
        Set<Integer> setOfExpectedFriends = Set.of(2, 4);
        Assertions.assertEquals(setOfExpectedFriends, user.getFriends(), "Друзья пользователя " + user.getName() + " не соответствуют ожидаемому");
    }

    @DisplayName("Добавление друга к несуществующему пользователю")
    @Test
    void addFriendToIncorrectUser() {
        User user = new User("user@email.com", "Login", "Степашка", LocalDate.of(1993, 9, 29));
        User user2 = new User("user2@email.com", "Login2", "Адам", LocalDate.of(1995, 9, 29));
        User user3 = new User("user3@email.com", "Login3", "Ева", LocalDate.of(1995, 2, 15));
        User user4 = new User("user4@email.com", "Login4", "Джон", LocalDate.of(1995, 2, 25));
        inMemoryUserStorage.addOrUpdateUser(user);
        inMemoryUserStorage.addOrUpdateUser(user2);
        inMemoryUserStorage.addOrUpdateUser(user3);
        inMemoryUserStorage.addOrUpdateUser(user4);
        Assertions.assertThrows(NotFoundException.class, () -> inMemoryUserStorage.addFriend(5, 2), "Исключение не прошло, пользователь найден");
    }

    @DisplayName("Добавление друга к несуществующему пользователю")
    @Test
    void getCommonFriends() {
        User user = new User("user@email.com", "Login", "Степашка", LocalDate.of(1993, 9, 29));
        User user2 = new User("user2@email.com", "Login2", "Адам", LocalDate.of(1995, 9, 29));
        User user3 = new User("user3@email.com", "Login3", "Ева", LocalDate.of(1995, 2, 15));
        User user4 = new User("user4@email.com", "Login4", "Джон", LocalDate.of(1995, 2, 25));
        User user5 = new User("user@email.com", "Login", "Иван", LocalDate.of(1993, 9, 29));
        User user6 = new User("user2@email.com", "Login2", "Пётр", LocalDate.of(1995, 9, 29));
        User user7 = new User("user3@email.com", "Login3", "Кирилл", LocalDate.of(1995, 2, 15));
        User user8 = new User("user4@email.com", "Login4", "Мефодий", LocalDate.of(1995, 2, 25));
        inMemoryUserStorage.addOrUpdateUser(user);
        inMemoryUserStorage.addOrUpdateUser(user2);
        inMemoryUserStorage.addOrUpdateUser(user3);
        inMemoryUserStorage.addOrUpdateUser(user4);
        inMemoryUserStorage.addOrUpdateUser(user5);
        inMemoryUserStorage.addOrUpdateUser(user6);
        inMemoryUserStorage.addOrUpdateUser(user7);
        inMemoryUserStorage.addOrUpdateUser(user8);
        for (int i = 2; i <= inMemoryUserStorage.findAll().size(); i++) {
            inMemoryUserStorage.addFriend(1, i);
        }
        for (int i = 1; i <= inMemoryUserStorage.findAll().size() - 2; i++) {
            inMemoryUserStorage.addFriend(7, i);
        }
        List<String> commonFriends = List.of("Адам", "Ева", "Джон", "Иван", "Пётр");
        Assertions.assertEquals(commonFriends, inMemoryUserStorage.getCommonFriends(1, 7).stream().map(User::getName).collect(Collectors.toList()));
    }

    @DisplayName("Добавление в друзья самого себя")
    @Test
    void addToFriendsYourself() {
        User user = new User("user@email.com", "Login", "Степашка", LocalDate.of(1993, 9, 29));
        inMemoryUserStorage.addOrUpdateUser(user);
        Assertions.assertThrows(ValidationException.class, () -> inMemoryUserStorage.addFriend(1, 1), "Исключение не прошло");
    }

}
