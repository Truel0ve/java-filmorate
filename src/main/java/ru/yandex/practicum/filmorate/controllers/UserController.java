package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utilities.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int newId = 0;
    private final UserValidator userValidator = new UserValidator();

    @GetMapping
    public Map<Integer, User> getUsers() {
        checkLogDebug(RequestMethod.GET);
        return users;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        checkLogDebug(RequestMethod.POST);
        try {
            if (validateUser(user)) {
                log.info("Пользователь с E-mail " + user.getEmail() + " уже есть в базе.");
                return users.get(user.getId());
            } else {
                users.put(user.getId(), user);
                log.info("Добавлен новый пользователь с E-mail " + user.getEmail());
                return user;
            }
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        checkLogDebug(RequestMethod.PUT);
        try {
            if (validateUser(user)) {
                users.replace(user.getId(), user);
                log.info("Внесены изменения в данные пользователя с E-mail " + user.getEmail());
            } else {
                users.put(user.getId(), user);
                log.info("Добавлен новый пользователь с E-mail " + user.getEmail());
            }
            return user;
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    private void checkLogDebug(RequestMethod requestMethod) {
        log.debug("Получен запрос " + requestMethod + "/users.");
    }

    public boolean validateUser(User user) throws ValidationException {
        return userValidator.isUserValid(user);
    }

    private class UserValidator {
        private boolean isUserValid(User user) throws ValidationException {
            validateNotNull(user);
            validateEmail(user);
            validateLogin(user);
            validateName(user);
            validateBirthday(user);
            return doesTheUserExist(user);
        }

        private void validateNotNull(User user) throws ValidationException {
            if (user == null) {
                throw new ValidationException("Данные пользователя не указаны.");
            }
        }

        private void validateEmail(User user) throws ValidationException {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("E-mail не указан.");
            }
            if (!user.getEmail().contains("@")) {
                throw new ValidationException("Неверно указан E-mail.");
            }
        }

        private void validateLogin(User user) throws ValidationException {
            if (user.getLogin() == null || user.getLogin().isBlank()) {
                throw new ValidationException("Логин не указан.");
            }
        }

        private void validateName(User user){
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
        }

        private void validateBirthday(User user) throws ValidationException {
            if (user.getBirthday() == null) {
                throw new ValidationException("Дата рождения не указана.");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Неверно указана дата рождения.");
            }
        }

        private boolean doesTheUserExist(User newUser) {
            if (!users.isEmpty()) {
                for (User user : users.values()) {
                    if (user.getEmail().equals(newUser.getEmail())) {
                        newUser.setId(user.getId());
                        return true;
                    }
                }
            }
            newUser.setId(++newId);
            return false;
        }
    }
}