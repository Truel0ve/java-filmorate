package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.utility.DefaultLogger;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FriendService friendService;
    private final FilmService filmService;
    private final EventService eventService;
    private final DefaultLogger defaultLogger;

    @GetMapping
    public List<User> getAllUsers() {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/users/" + userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        defaultLogger.logRequestMethod(RequestMethod.POST, "/users");
        return userService.createUser(user);
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        defaultLogger.logRequestMethod(RequestMethod.PUT, "/users");
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.DELETE, "/users/" + userId);
        userService.deleteUser(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        defaultLogger.logRequestMethod(RequestMethod.PUT, "/users/" + userId + "/friends/" + friendId);
        friendService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        defaultLogger.logRequestMethod(RequestMethod.DELETE, "/users/" + userId + "/friends/" + friendId);
        friendService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable("id") Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/users/" + userId + "/friends");
        return friendService.getFriendList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriendList(@PathVariable("id") Long userId, @PathVariable Long otherId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/users/" + userId + "/friends/common/" + otherId);
        return friendService.getCommonFriendList(userId, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getAllEvents(@PathVariable("id") Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/users/" + userId + "/feed");
        return eventService.getAllEvents(userId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable("id") Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/users/" + userId + "/recommendations");
        return filmService.getRecommendations(userId);
    }
}