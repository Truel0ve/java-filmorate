package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService implements GenreStorage {
    private final GenreDbStorage genreStorage;
    private final IdValidator idValidator;

    // Получить жанр фильма по ID
    @Override
    public Genre getGenreById(Long genreId) {
        idValidator.validateGenreId(genreId);
        return genreStorage.getGenreById(genreId);
    }

    // Получить все доступные жанры фильмов
    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }
}