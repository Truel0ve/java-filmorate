package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @GetMapping
    public List<User> getAllUsers() {
        logRequestMethod(RequestMethod.GET);
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long userId) {
        logRequestMethod(RequestMethod.GET, "/" + userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        logRequestMethod(RequestMethod.POST);
        return userService.createUser(user);
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        logRequestMethod(RequestMethod.PUT);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        logRequestMethod(RequestMethod.DELETE);
        userService.deleteUser(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        logRequestMethod(RequestMethod.PUT, "/" + userId + "/friends/" + friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        logRequestMethod(RequestMethod.DELETE, "/" + userId + "/friends/" + friendId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable("id") Long userId) {
        logRequestMethod(RequestMethod.GET, "/" + userId + "/friends");
        return userService.getFriendList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriendList(@PathVariable("id") Long userId, @PathVariable Long otherId) {
        logRequestMethod(RequestMethod.GET, "/" + userId + "/friends/common/" + otherId);
        return userService.getCommonFriendList(userId, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable("id") Long userId) {
        logRequestMethod(RequestMethod.GET, "/" + userId + "/recommendation");
        return filmService.getRecommendations(userId);
    }

    private void logRequestMethod(RequestMethod requestMethod) {
        log.debug("Получен запрос " + requestMethod + " по адресу: /users");
    }

    private void logRequestMethod(RequestMethod requestMethod, String path) {
        log.debug("Получен запрос " + requestMethod + " по адресу: /users" + path);
    }
}