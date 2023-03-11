package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.database.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmLikeStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;

@Service
@AllArgsConstructor
public class FilmLikeService implements FilmLikeStorage {
    private final FilmLikeDbStorage filmLikeStorage;
    private final EventService eventService;
    private final IdValidator idValidator;

    // Поставить лайк фильму
    @Override
    public void addLike(Long filmId, Long userId) {
        idValidator.validateFilmId(filmId);
        idValidator.validateUserId(userId);
        filmLikeStorage.addLike(filmId, userId);
        eventService.addEvent(userId, filmId, "LIKE", "ADD");
    }

    // Удалить лайк фильму
    @Override
    public void deleteLike(Long filmId, Long userId) {
        idValidator.validateFilmId(filmId);
        idValidator.validateUserId(userId);
        filmLikeStorage.deleteLike(filmId, userId);
        eventService.addEvent(userId, filmId, "LIKE", "REMOVE");
    }
}