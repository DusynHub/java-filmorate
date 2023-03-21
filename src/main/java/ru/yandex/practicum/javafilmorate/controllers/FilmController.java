package ru.yandex.practicum.javafilmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;


    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        log.info("Получен запрос 'POST /films'");
        return filmService.addNewFilmToStorage(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        log.info("Получен запрос 'PUT /films'");
        return filmService.updateFilmInStorage(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable long id, @PathVariable long userId) {
        log.info(String.format("Получен запрос 'PUT /films/%d/like/%d'", id, userId));
        filmService.likeFilmInStorage(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable long id, @PathVariable long userId) {
        log.info(String.format("Получен запрос 'DELETE /films/%d/like/%d'", id, userId));
        filmService.deleteLikeFilmInStorage(id, userId);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        log.info(String.format("Получен запрос 'GET /films/%d'", id));
        return filmService.getFilmFromStorage(id);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Получен запрос 'GET /films'");
        return filmService.getAllFilmsFromStorage();
    }

    @GetMapping("/popular")
    public List<Film> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        log.info(String.format("Получен запрос 'GET /films/popular?count=%d'", count));
        return filmService.getMostLikedFilmsFromStorage(count);
    }

}
