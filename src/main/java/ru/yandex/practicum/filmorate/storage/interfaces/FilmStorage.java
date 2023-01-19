package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;


public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Film film);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}