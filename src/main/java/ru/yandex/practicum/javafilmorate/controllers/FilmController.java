package ru.yandex.practicum.javafilmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

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

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        log.info(String.format("Получен запрос 'GET /films/common?userId=%d&friendId=%d'", userId, friendId));
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> getSearchFilms(@RequestParam(name = "query") String query,
                                     @RequestParam(name = "by") List<String> titleOrDirector) {
        log.info("Получен запрос 'GET/films/search?query = {} & by = {} ", query, titleOrDirector);
        return filmService.getSearchFilms(query, titleOrDirector);
    }

}
