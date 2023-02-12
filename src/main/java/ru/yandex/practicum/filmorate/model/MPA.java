package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class MPA {
    private final int id;

    public MPA(TypeMPA typeMPA) {
        this.id = typeMPA.ordinal() + 1;
    }

    @Getter
    private enum TypeMPA {
        G ("G"),
        PG ("PG"),
        PG13 ("PG-13"),
        R ("R"),
        NC17 ("NC-17");

        private final String name;

        TypeMPA(String name) {
            this.name = name;
        }
    }
}