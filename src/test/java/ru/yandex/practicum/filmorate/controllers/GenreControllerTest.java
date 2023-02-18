package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreControllerTest {
    private final GenreController genreController;

    @Test
    void shouldReturnGenres() {
        assertEquals(Genre.GenreType.values().length, genreController.getAllGenres().size(),
                "Неверное количество жанров в списке.");
    }

    @Test
    void shouldReturnGenreById() {
        assertEquals(new Genre(1, "Комедия"), genreController.getGenreById(1),
                "Фильм не соответствует ожидаемому значению.");
    }
}
