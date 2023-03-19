package ru.yandex.practicum.javafilmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.model.FilmSort;
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

    @DeleteMapping("/{filmId}")
    public Film deleteFilmById(@PathVariable long filmId) {
        log.info("Получен запрос 'DELETE /films'");
        return filmService.deleteFilmFromStorage(filmId);
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
    public List<Film> getMostPopularsFilm(@RequestParam(defaultValue = "10") int count,
                                          @RequestParam(required = false) Long genreId,
                                          @RequestParam(required = false) Integer year) {
        log.info(String.format("Получен запрос 'GET /films/popular?count=%d&genreId=%d&year=%d'", count, genreId, year));
        return filmService.getMostPopularsFilmsByGenreByYear(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable long directorId, @RequestParam String sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

}