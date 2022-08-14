package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.HashSet;

public class UserControllerTest {
    UserController userController = new UserController();

    @Test
    public void shouldCreateNewUser() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        User newUser = userController.create(user);
        user.setId(newUser.getId());
        Assertions.assertEquals(user, newUser);
    }

    @Test
    public void shouldNotCreateNewUserNoEmail() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        });
    }

    @Test
    public void shouldNotCreateNewUserNoEmailSymbol() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        });
    }

    @Test
    public void shouldNotCreateNewUserNoLogin() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        });
    }

    @Test
    public void shouldNotCreateNewUserLoginWithSpace() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "login Test", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        });
    }

    @Test
    public void shouldCreateNewUserWithSameNameAndLogin() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "", birthdayTest, new HashSet<>());
        User newUser = userController.create(user);
        user.setId(newUser.getId());
        user.setName(user.getLogin());
        Assertions.assertEquals(user, newUser);
    }

    @Test
    public void shouldNotCreateNewUserBirthdayFuture() {
        LocalDate birthdayTest = LocalDate.of(2100, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        });
    }

    @Test
    public void shouldUpdateUser() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        User newUser = userController.create(user);
        user.setId(newUser.getId());
        User updateUser = new User(newUser.getId(),
                "email@email.ru",
                "loginTestUpdate",
                "name test",
                birthdayTest,
                new HashSet<>());
        Assertions.assertEquals(updateUser, userController.update(updateUser));
    }

    @Test
    public void shouldUpdateUserNoEmail() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "", "loginTestUpdate",
                    "name test", birthdayTest, new HashSet<>());
            userController.update(updateUser);
        });
    }

    @Test
    public void shouldUpdateUserNoEmailSymbol() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "", "loginTestUpdate",
                    "name test", birthdayTest, new HashSet<>());
            userController.update(updateUser);
        });
    }

    @Test
    public void shouldUpdateUserNoLogin() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "", "loginTestUpdate",
                    "name test", birthdayTest, new HashSet<>());
            userController.update(updateUser);
        });
    }

    @Test
    public void shouldUpdateUserLoginWithSpace() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "", "loginTestUpdate",
                    "name test", birthdayTest, new HashSet<>());
            userController.update(updateUser);
        });
    }

    @Test
    public void shouldUpdateUserWithSameNameAndLogin() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "", "loginTestUpdate",
                    "name test", birthdayTest, new HashSet<>());
            userController.update(updateUser);
        });
    }

    @Test
    public void shouldUpdateUserBirthdayFuture() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest, new HashSet<>());
        Assertions.assertThrows(ValidationException.class, () -> {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "", "loginTestUpdate",
                    "name test", birthdayTest, new HashSet<>());
            userController.update(updateUser);
        });
    }
}
