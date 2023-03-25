package ru.yandex.practicum.javafilmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.model.Mpa;
import ru.yandex.practicum.javafilmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> getAll() {
        return mpaService.getAllMpaFromDb();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable long id) {
        return mpaService.getMpaByIdFromDb(id);
    }
}
