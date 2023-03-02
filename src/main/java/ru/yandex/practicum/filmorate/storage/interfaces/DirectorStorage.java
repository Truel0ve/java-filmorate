package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);

    Director getDirectorById(Long directorId);

    List<Director> getAllDirectors();

    List<Film> getDirectorsFilms(Long directorId);
}
