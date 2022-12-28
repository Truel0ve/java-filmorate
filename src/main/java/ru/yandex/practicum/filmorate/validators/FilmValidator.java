package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

public class FilmValidator {
    public void validate(Film film) throws ValidationException {
        validateNotNull(film);
        validateName(film);
        validateDescription(film);
        validateReleaseDate(film);
        validateDuration(film);
    }

    private void validateNotNull(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("Данные фильма не указаны.");
        }
    }

    private void validateName(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не указано.");
        }
    }

    private void validateDescription(Film film) throws ValidationException {
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ValidationException("Описание фильма не указано.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может превышать 200 символов.");
        }
    }

    private void validateReleaseDate(Film film) throws ValidationException {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не указана.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
    }

    private void validateDuration(Film film) throws ValidationException {
        if (film.getDuration() == null) {
            throw new ValidationException("Продолжительность фильма не указана.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма не может быть меньше или равна 0.");
        }
    }
}