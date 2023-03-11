package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaService implements MpaStorage {
    private final MpaDbStorage mpaStorage;
    private final IdValidator idValidator;

    // Получить MPA-рейтинг фильма по ID
    @Override
    public Mpa getMpaById(Long mpaId) {
        idValidator.validateMpaId(mpaId);
        return mpaStorage.getMpaById(mpaId);
    }

    // Получить список всех доступных MPA-рейтингов фильмов
    @Override
    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}