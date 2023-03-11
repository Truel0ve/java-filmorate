package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.utility.DefaultLogger;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;
    private final DefaultLogger defaultLogger;

    @GetMapping
    public List<Director> getAllDirectors() {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/directors");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") Long directorId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/directors/" + directorId);
        return directorService.getDirectorById(directorId);
    }

    @PostMapping
    public Director postDirector(@Valid @RequestBody Director director) {
        defaultLogger.logRequestMethod(RequestMethod.POST, "/directors");
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director putDirector(@Valid @RequestBody Director director) {
        defaultLogger.logRequestMethod(RequestMethod.PUT, "/directors");
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") Long directorId) {
        defaultLogger.logRequestMethod(RequestMethod.DELETE, "/directors/" + directorId);
        directorService.deleteDirector(directorId);
    }
}