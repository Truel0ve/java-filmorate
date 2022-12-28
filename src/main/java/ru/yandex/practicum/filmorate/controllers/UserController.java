package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        logRequestMethod(RequestMethod.GET);
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer userId) {
        String path = "/" + userId;
        logRequestMethod(RequestMethod.GET, path);
        return userService.getUserById(Long.valueOf(userId));
    }

    @PostMapping
    public User post(@Valid @RequestBody User user) {
        logRequestMethod(RequestMethod.POST);
        return userService.createUser(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        logRequestMethod(RequestMethod.PUT);
        return userService.updateUser(user);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody User user) {
        logRequestMethod(RequestMethod.DELETE);
        userService.deleteUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        String path = "/" + userId + "/friends/" + friendId;
        logRequestMethod(RequestMethod.PUT, path);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        String path = "/" + userId + "/friends/" + friendId;
        logRequestMethod(RequestMethod.DELETE, path);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable("id") Long userId) {
        String path = "/" + userId + "/friends";
        logRequestMethod(RequestMethod.GET, path);
        return userService.getFriendList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriendList(@PathVariable("id") Long userId, @PathVariable Long otherId) {
        String path = "/" + userId + "/friends/common/" + otherId;
        logRequestMethod(RequestMethod.GET, path);
        return userService.getMutualFriendList(userId, otherId);
    }

    private void logRequestMethod(RequestMethod requestMethod) {
        log.debug("Получен запрос " + requestMethod + " по адресу: /users");
    }

    private void logRequestMethod(RequestMethod requestMethod, String path) {
        log.debug("Получен запрос " + requestMethod + " по адресу: /users" + path);
    }
}