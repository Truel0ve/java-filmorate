package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
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
    private Set<Genre> genres;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Film film = (Film) obj;
        return name.equals(film.getName()) &&
                description.equals(film.getDescription()) &&
                releaseDate.equals(film.getReleaseDate()) &&
                duration.equals(film.getDuration()) &&
                mpa.equals(film.getMpa());
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

    @Override
    public int compareTo(Film film) {
        int compareLikes = Long.compare(film.getLikes().size(), likes.size());
        int compareId = Long.compare(id, film.getId());
        return (compareLikes != 0) ? compareLikes : compareId;
    }
}
