package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.database.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DirectorService implements DirectorStorage {
    private final DirectorDbStorage directorStorage;
    private final IdValidator idValidator;

    // Создать нового режиссёра
    @Override
    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    // Обновить данные режиссёра
    @Override
    public Director updateDirector(Director director) {
        idValidator.validateDirectorId(director.getId());
        return directorStorage.updateDirector(director);
    }

    // Удалить режиссёра
    @Override
    public void deleteDirector(Long directorId) {
        idValidator.validateDirectorId(directorId);
        directorStorage.deleteDirector(directorId);
    }

    // Получить данные режиссёра по ID
    @Override
    public Director getDirectorById(Long directorId) {
        idValidator.validateDirectorId(directorId);
        return directorStorage.getDirectorById(directorId);
    }

    // Получить список всех режиссёров
    @Override
    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    // Получить список всех фильмов режиссёра
    @Override
    public List<Film> getDirectorsFilms(Long directorId) {
        idValidator.validateDirectorId(directorId);
        return directorStorage.getDirectorsFilms(directorId);
    }

    // Получить список всех фильмов режиссёра, отсортированных по годам или количеству лайков
    public List<Film> getDirectorsFilms(Long directorId, String sortBy) {
        List<Film> directorsFilms = getDirectorsFilms(directorId);
        if (!directorsFilms.isEmpty()) {
            switch (sortBy) {
                case "year":
                    return directorsFilms.stream()
                            .sorted(Comparator.comparing(Film::getReleaseDate))
                            .collect(Collectors.toList());
                case "likes":
                    return directorsFilms.stream()
                            .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
                            .collect(Collectors.toList());
                default:
                    throw new ArgumentNotFoundException("Неверный параметр запроса");
            }
        } else {
            throw new ArgumentNotFoundException("В базе нет фильмов выбранного режиссёра");
        }
    }
}