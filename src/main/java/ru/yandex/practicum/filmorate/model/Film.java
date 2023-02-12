package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film implements Comparable<Film> {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private Integer duration;
    private Set<Long> likes = new HashSet<>();
    private Long rate;
    private int mpaId;
    private List<Genre> genres = new ArrayList<>();

    public Film(String name, String description, LocalDate releaseDate, Integer duration, int mpaId) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaId = mpaId;
    }

    @Override
    public int compareTo(Film film) {
        return (this.likes.size() > film.getLikes().size()) ? -1 : 1;
    }
}
