package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public abstract class AbstractData {
    private final Long id;
    private final String name;
}