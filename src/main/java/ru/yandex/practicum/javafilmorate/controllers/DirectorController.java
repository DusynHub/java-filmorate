package ru.yandex.practicum.javafilmorate.controllers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.model.Director;
import ru.yandex.practicum.javafilmorate.service.DirectorService;
import ru.yandex.practicum.javafilmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> getAllDirectors() {
            log.info("Получен запрос GET /directors");
            return directorService.getAllDirectorFromDb();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return directorService.getDirectorFromDbById(id);
    }

    @PostMapping
    public Director createDirector(@RequestBody @Valid Director director) {
        log.info("Получен запрос POST /directors");
        return directorService.createDirectorInDb(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info("Получен запрос PUT /directors");
        return directorService.updateDirectorInDb(director);
    }

    @DeleteMapping("/{id}")
    public Director deleteDirectorById(@PathVariable long id) {
        log.info("Получен запрос DELETE /directors");
        return directorService.deleteDirectorInDb(id);
    }
}
