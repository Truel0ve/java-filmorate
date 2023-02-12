package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class Genre {
    private final int id;

    public Genre(TypeGenre typeGenre) {
        this.id = typeGenre.ordinal() + 1;
    }

    @Getter
    private enum TypeGenre {
        COMEDY ("Комедия"),
        DRAMA ("Драма"),
        CARTOON ("Мультфильм"),
        THRILLER ("Триллер"),
        DOCUMENTARY ("Документальный"),
        ACTION ("Боевик");

        private final String name;

        TypeGenre(String name) {
            this.name = name;
        }
    }
}