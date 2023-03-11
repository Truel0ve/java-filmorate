package ru.yandex.practicum.filmorate.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.storage.database.*;

@Component
@RequiredArgsConstructor
public class IdValidator {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final DirectorDbStorage directorStorage;
    private final ReviewDbStorage reviewStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    public void validateUserId(Long userId) {
        if (userId == null || userStorage.getUserById(userId) == null) {
            throw new ArgumentNotFoundException("ID пользователя не задан или отсутствует в базе");
        }
    }

    public void validateFilmId(Long filmId) {
        if (filmId == null || filmStorage.getFilmById(filmId) == null) {
            throw new ArgumentNotFoundException("ID фильма не задан или отсутствует в базе");
        }
    }

    public void validateDirectorId(Long directorId) {
        if (directorId == null || directorStorage.getDirectorById(directorId) == null) {
            throw new ArgumentNotFoundException("ID режиссера не задан или отсутствует в базе");
        }
    }

    public void validateReviewId(Long reviewId) {
        if (reviewId == null || reviewStorage.getReviewById(reviewId) == null) {
            throw new ArgumentNotFoundException("ID отзыва не задан или отсутствует в базе");
        }
    }

    public void validateMpaId(Long mpaId) {
        if (mpaId == null || mpaStorage.getMpaById(mpaId) == null) {
            throw new ArgumentNotFoundException("ID рейтинга MPA не задан или отсутствует в базе");
        }
    }

    public void validateGenreId(Long genreId) {
        if (genreId == null || genreStorage.getGenreById(genreId) == null) {
            throw new ArgumentNotFoundException("ID жанра не задан или отсутствует в базе");
        }
    }
}