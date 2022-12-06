package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utilities.ValidationException;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final List<User> users = new ArrayList<>();
    private int newId = 0;
    private final UserHandler userHandler = new UserHandler();

    @GetMapping
    public List<User> getUsers() {
        userHandler.logRequestMethod(RequestMethod.GET);
        return users;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userHandler.handleRequest(user, RequestMethod.POST);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userHandler.handleRequest(user, RequestMethod.PUT);
    }

    private class UserHandler {
        private final UserValidator userValidator = new UserValidator();

        private User handleRequest(User user, RequestMethod requestMethod) {
            logRequestMethod(requestMethod);
            try {
                userValidator.validateUser(user);
                return handleUser(user, requestMethod);
            } catch (ValidationException e) {
                log.warn(e.getMessage(), e);
                return null;
            }
        }

        private void logRequestMethod(RequestMethod requestMethod) {
            log.debug("Получен запрос " + requestMethod + "/users.");
        }

        private User handleUser(User newUser, RequestMethod requestMethod) {
            if (!users.isEmpty()) {
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    if (user.getEmail().equals(newUser.getEmail())) {
                        switch (requestMethod) {
                            case POST:
                                log.info("Пользователь с E-mail " + user.getEmail() + " уже есть в базе.");
                                return user;
                            case PUT:
                                newUser.setId(++newId);
                                users.set(i, newUser);
                                log.info("Внесены изменения в данные пользователя с E-mail " + newUser.getEmail());
                                return newUser;
                        }
                    }
                }
            }
            newUser.setId(++newId);
            users.add(newUser);
            log.info("Добавлен новый пользователь с E-mail " + newUser.getEmail());
            return newUser;
        }
    }
}