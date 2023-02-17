package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
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
    private Set<Long> likes;
    private Long rate;
    private Mpa mpa;
    private List<Genre> genres;

    @Override
    public int compareTo(Film film) {
        return (this.likes.size() > film.getLikes().size()) ? -1 : 1;
    }
}
