package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.InvalidBirthdayException;
import ru.yandex.practicum.exceptions.InvalidEmailException;
import ru.yandex.practicum.exceptions.InvalidLoginException;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;

public class UserControllerTest {
    UserController userController = new UserController();

    @Test
    public void shouldCreateNewUser() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            Assertions.assertEquals(user, newUser);
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateNewUserNoEmail() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidEmailException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotCreateNewUserNoEmailSymbol() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidEmailException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotCreateNewUserNoLogin() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidLoginException.class, e.getClass());
        }
    }

    @Test
    public void shouldNotCreateNewUserLoginWithSpace() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "login Test", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidLoginException.class, e.getClass());
        }
    }

    @Test
    public void shouldCreateNewUserWithSameNameAndLogin() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            user.setName(user.getLogin());
            Assertions.assertEquals(user, newUser);
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateNewUserBirthdayFuture() {
        LocalDate birthdayTest = LocalDate.of(2100, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidBirthdayException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateUser() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "email@email.ru", "loginTestUpdate",
                    "name test", birthdayTest);
            Assertions.assertEquals(updateUser, userController.update(updateUser));
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldUpdateUserNoEmail() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "", "loginTestUpdate",
                    "name test", birthdayTest);
            userController.update(updateUser);
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidEmailException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateUserNoEmailSymbol() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "email.ru", "loginTestUpdate",
                    "name test", birthdayTest);
            userController.update(updateUser);
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidEmailException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateUserNoLogin() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "email@email.ru", "",
                    "name test", birthdayTest);
            userController.update(updateUser);
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidLoginException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateUserLoginWithSpace() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "email@email.ru", "loginTest Update",
                    "name test", birthdayTest);
            userController.update(updateUser);
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidLoginException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateUserWithSameNameAndLogin() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "email@email.ru", "loginTestUpdate",
                    "", birthdayTest);
            userController.update(updateUser);
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidLoginException.class, e.getClass());
        }
    }

    @Test
    public void shouldUpdateUserBirthdayFuture() {
        LocalDate birthdayTest = LocalDate.of(2000, 5, 15);
        User user = new User(10, "email@email.ru", "loginTest", "name test", birthdayTest);
        try {
            User newUser = userController.create(user);
            user.setId(newUser.getId());
            User updateUser = new User(newUser.getId(), "email@email.ru", "loginTestUpdate",
                    "name test", LocalDate.of(2100, 5, 15));
            userController.update(updateUser);
        } catch (InvalidEmailException | InvalidLoginException | InvalidBirthdayException e) {
            Assertions.assertEquals(InvalidBirthdayException.class, e.getClass());
        }
    }
}
