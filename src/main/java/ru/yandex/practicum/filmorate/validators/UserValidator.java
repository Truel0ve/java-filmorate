package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utilities.ValidationException;

import java.time.LocalDate;

public class UserValidator {
    public void validateUser(User user) throws ValidationException {
        validateNotNull(user);
        validateEmail(user);
        validateLogin(user);
        validateName(user);
        validateBirthday(user);
    }

    private void validateNotNull(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Данные пользователя не указаны.");
        }
    }

    private void validateEmail(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("E-mail не указан.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Неверно указан E-mail.");
        }
    }

    private void validateLogin(User user) throws ValidationException {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не указан.");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин.");
        }
    }

    private void validateName(User user){
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateBirthday(User user) throws ValidationException {
        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения не указана.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Неверно указана дата рождения.");
        }
    }
}
