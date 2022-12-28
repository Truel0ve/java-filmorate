package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getAll();

    Film create(Film film);

    Film update(Film film);

    void delete(Film film);
}
