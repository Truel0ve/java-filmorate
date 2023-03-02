package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.database.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    // Создать нового режиссёра
    @Override
    public Director createDirector(Director director) {
        try {
            String sqlInsertDirector =
                    "INSERT INTO directors (director_name) " +
                    "VALUES (?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement preparedStatement = con.prepareStatement(sqlInsertDirector, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, director.getName());
                return preparedStatement;
            }, keyHolder);
            Long directorId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            director.setId(directorId);
            log.info("Добавлен новый режиссёр с ID=" + directorId + ".");
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Режиссёр с именем " + director.getName() + " уже есть в базе.");
        }
        return director;
    }

    // Обновить данные режиссёра
    @Override
    public Director updateDirector(Director director) {
        String sqlUpdateDirector =
                "UPDATE directors " +
                "SET director_name = ?2 " +
                "WHERE director_id = ?1";
        jdbcTemplate.update(sqlUpdateDirector, director.getId(), director.getName());
        log.info("Внесены изменения в данные режиссёра с ID=" + director.getId() + ".");
        return director;
    }

    // Удалить режиссёра
    @Override
    public void deleteDirector(Long directorId) {
        String sqlDeleteDirector =
                "DELETE FROM directors " +
                "WHERE director_id = ?";
        jdbcTemplate.update(sqlDeleteDirector, directorId);
        String sqlDeleteList =
                "DELETE FROM director_list " +
                "WHERE director_id = ?";
        jdbcTemplate.update(sqlDeleteList, directorId);
        log.info("Режиссёр с ID=" + directorId + " удален из базы.");
    }

    // Получить данные режиссёра по ID
    @Override
    public Director getDirectorById(Long directorId) {
        String sqlSelectDirector =
                "SELECT * " +
                "FROM directors " +
                "WHERE director_id = ?";
        List<Director> directorList = jdbcTemplate.query(sqlSelectDirector,
                (rs, rowNum) -> new Director(rs.getLong("director_id"), rs.getString("director_name")), directorId);
        return directorList.stream()
                .findFirst()
                .orElseThrow(() -> new ArgumentNotFoundException("Режиссёр с ID=" + directorId + " отсутствует в базе"));
    }

    // Получить список всех режиссёров
    @Override
    public List<Director> getAllDirectors() {
        String sqlSelectAll =
                "SELECT * " +
                "FROM directors " +
                "ORDER BY director_id ASC";
        return jdbcTemplate.query(sqlSelectAll,
                (rs, rowNum) -> new Director(rs.getLong("director_id"), rs.getString("director_name")));
    }

    // Получить список всех фильмов режиссёра
    @Override
    public List<Film> getDirectorsFilms(Long directorId) {
        String sqlSelectAllFilms =
                "SELECT f.*, m.mpa_name, " +
                "GROUP_CONCAT (DISTINCT g.genre_id ORDER BY g.genre_id SEPARATOR ',') AS genre_id, " +
                "GROUP_CONCAT (DISTINCT g.genre_name ORDER BY g.genre_id SEPARATOR ',') AS genre_name, " +
                "GROUP_CONCAT (DISTINCT ll.user_id ORDER BY ll.user_id SEPARATOR ',') AS likes, " +
                "GROUP_CONCAT (DISTINCT d.director_id ORDER BY d.director_id SEPARATOR ',') AS director_id, " +
                "GROUP_CONCAT (DISTINCT d.director_name ORDER BY d.director_id SEPARATOR ',') AS director_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN genre_list AS gl ON gl.film_id = f.film_id " +
                "LEFT JOIN genres AS g ON g.genre_id = gl.genre_id " +
                "LEFT JOIN director_list AS dl ON dl.film_id = f.film_id " +
                "LEFT JOIN directors AS d ON d.director_id = dl.director_id " +
                "LEFT JOIN like_list AS ll ON ll.film_id = f.film_id " +
                "WHERE d.director_id = ?" +
                "GROUP BY f.film_id " +
                "ORDER BY f.film_id";
        return jdbcTemplate.query(sqlSelectAllFilms, new FilmRowMapper(), directorId);
    }

    // Сохранить переданных режиссеров фильма
    public Set<Director> setDirectors(Film film) {
        return updateDirectors(validateDirectors(film), film);
    }

    // Проверить и отсортировать режиссеров передаваемого фильма
    private Set<Director> validateDirectors(Film film) {
        return film.getDirectors().stream()
                .map(Director::getId)
                .distinct()
                .filter(id -> id > 0 && id <= getAllDirectors().size())
                .sorted()
                .map(this::getDirectorById)
                .collect(Collectors.toSet());
    }

    // Сохранить пакет ID режиссеров и фильмов в базу данных
    private Set<Director> updateDirectors(Set<Director> directors, Film film) {
        List<Object[]> batch = new ArrayList<>();
        for (Director director : directors) {
            Object[] values = new Object[] {
                    director.getId(),
                    film.getId()};
            batch.add(values);
        }
        String sqlInsertDirectors =
                "INSERT INTO director_list (director_id, film_id) " +
                "VALUES (?,?)";
        jdbcTemplate.batchUpdate(sqlInsertDirectors, batch);
        return directors;
    }
}