package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/directors")
@Slf4j
@Getter
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> getAllDirectors() {
        logRequestMethod(RequestMethod.GET);
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") Long directorId) {
        logRequestMethod(RequestMethod.GET, "/" + directorId);
        return directorService.getDirectorById(directorId);
    }

    @PostMapping
    public Director postDirector(@Valid @RequestBody Director director) {
        logRequestMethod(RequestMethod.POST);
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director putDirector(@Valid @RequestBody Director director) {
        logRequestMethod(RequestMethod.PUT);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") Long directorId) {
        logRequestMethod(RequestMethod.DELETE, "/" + directorId);
        directorService.deleteDirector(directorId);
    }

    private void logRequestMethod(RequestMethod requestMethod) {
        log.debug("Получен запрос " + requestMethod + " по адресу: /directors");
    }

    private void logRequestMethod(RequestMethod requestMethod, String path) {
        log.debug("Получен запрос " + requestMethod + " по адресу: /directors" + path);
    }
}
