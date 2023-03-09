package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.database.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class DirectorService implements DirectorStorage {
    private final DirectorDbStorage directorStorage;

    // Создать нового режиссёра
    @Override
    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    // Обновить данные режиссёра
    @Override
    public Director updateDirector(Director director) {
        validateDirectorId(director.getId());
        return directorStorage.updateDirector(director);
    }

    // Удалить режиссёра
    @Override
    public void deleteDirector(Long directorId) {
        validateDirectorId(directorId);
        directorStorage.deleteDirector(directorId);
    }

    // Получить данные режиссёра по ID
    @Override
    public Director getDirectorById(Long directorId) {
        validateDirectorId(directorId);
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
        validateDirectorId(directorId);
        return directorStorage.getDirectorsFilms(directorId);
    }

    // Проверить корректность передаваемого ID режиссёра
    private void validateDirectorId(Long directorId) {
        if (directorId == null || directorStorage.getDirectorById(directorId) == null) {
            throw new ArgumentNotFoundException("ID режиссера не задан или отсутствует в базе.");
        }
    }
}