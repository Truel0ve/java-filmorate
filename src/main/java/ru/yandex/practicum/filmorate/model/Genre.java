package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;

@Data
public class Genre {
    private final int id;
    private final String name;

    @Getter
    public enum GenreType {
        COMEDY (1,"Комедия"),
        DRAMA (2,"Драма"),
        CARTOON (3,"Мультфильм"),
        THRILLER (4,"Триллер"),
        DOCUMENTARY (5,"Документальный"),
        ACTION (6,"Боевик");

        private final int id;
        private final String name;

        GenreType(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}