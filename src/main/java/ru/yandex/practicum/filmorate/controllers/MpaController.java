package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.utility.DefaultLogger;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;
    private final DefaultLogger defaultLogger;

    @GetMapping
    public List<Mpa> getAllMpa() {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/mpa");
        return mpaService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable("id") Long mpaId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/mpa/" + mpaId);
        return mpaService.getMpaById(mpaId);
    }
}