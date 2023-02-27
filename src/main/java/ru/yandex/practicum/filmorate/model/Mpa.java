package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

public class Mpa extends AbstractData {

    public Mpa(Long id, String name) {
        super(id, name);
    }

    @Getter
    public enum MpaType {
        G (1, "G"),
        PG (2,"PG"),
        PG13 (3,"PG-13"),
        R (4,"R"),
        NC17 (5,"NC-17");

        private final int id;
        private final String name;

        MpaType(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}