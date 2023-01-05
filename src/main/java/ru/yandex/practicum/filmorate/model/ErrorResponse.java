package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    private final String error;
    private User user;
    private Film film;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(String error, User user, Film film) {
        this.error = error;
        this.user = user;
        this.film = film;
    }

    public String getError() {
        return error;
    }

    public User getUser() {
        return user;
    }

    public Film getFilm() {
        return film;
    }
}
