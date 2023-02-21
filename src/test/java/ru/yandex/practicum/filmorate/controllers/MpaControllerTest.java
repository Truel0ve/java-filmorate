package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaControllerTest {
    private final MpaController mpaController;

    @Test
    void shouldReturnMpa() {
        assertEquals(Mpa.MpaType.values().length, mpaController.getAllMpa().size(),
                "Неверное количество жанров в списке.");
    }

    @Test
    void shouldReturnMpaById() {
        assertEquals(new Mpa(1, "G"), mpaController.getMpaById(1),
                "Фильм не соответствует ожидаемому значению.");
    }
}
