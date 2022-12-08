package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    private final Handler handler = new Handler();
    private int newId = 0;

    @GetMapping
    public List<User> get() {
        handler.logRequestMethod(RequestMethod.GET);
        return users;
    }

    @PostMapping
    public User post(@Valid @RequestBody User user) {
        return handler.handleRequest(user, RequestMethod.POST);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return handler.handleRequest(user, RequestMethod.PUT);
    }

    private class Handler {
        private final UserValidator userValidator = new UserValidator();

        private User handleRequest(User user, RequestMethod requestMethod) {
            logRequestMethod(requestMethod);
            try {
                userValidator.validate(user);
                return handle(user, requestMethod);
            } catch (ValidationException e) {
                log.warn(e.getMessage(), e);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        private void logRequestMethod(RequestMethod requestMethod) {
            log.debug("Получен запрос " + requestMethod + "/users.");
        }

        private User handle(User user, RequestMethod requestMethod) throws IllegalArgumentException {
            switch (requestMethod) {
                case POST:
                    return post(user);
                case PUT:
                    validateId(user);
                    return put(user);
                default:
                    throw new IllegalArgumentException("Запрашиваемый метод не поддерживается.\n" +
                            "Для добавления или изменения пользователя выберите POST- или PUT-запрос.");
            }
        }

        private User post(User newUser) {
            if (!users.isEmpty()) {
                for (User user : users) {
                    if (user.getEmail().equals(newUser.getEmail())) {
                        log.info("Пользователь с E-mail " + newUser.getEmail() + " уже есть в базе.");
                        throw new ResponseStatusException(HttpStatus.OK);
                    }
                }
                newUser.setId(++newId);
            } else {
                newUser.setId(++newId);
            }
            users.add(newUser);
            log.info("Добавлен новый пользователь с E-mail " + newUser.getEmail());
            return newUser;
        }

        private User put(User newUser) {
            if (!users.isEmpty()) {
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    if (user.getId().equals(newUser.getId())) {
                        users.set(i, newUser);
                        log.info("Внесены изменения в данные пользователя с ID=" + newUser.getId());
                        return newUser;
                    }
                }
            }
            log.info("Пользователя с указанным ID=" + newUser.getId() + " нет в базе.");
            throw new ResponseStatusException(HttpStatus.OK);
        }

        private void validateId(User newUser) {
            if (newUser.getId() == null || newUser.getId() > users.size()) {
                log.warn("ID пользователя не задан или отсутствует в базе.");
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}