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

    // GET /films/search?query=крад&by=director,title

    //   запросы из тестов
    //   GET/films/search?query=upDatE&by=title,director
    //   GET/films/search?query=не найти&by=director,title
    //   GET/films/search?query=UPdat&by=title
    //   GET/films/search?query=upDATE&by=director
    //   Проверить нужно ли применять toLowerCase для query

    @GetMapping("/films/search")
    public List<Film> getSearchFilms(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "by") List<String> titleOrDirector) {
        log.info("Получен запрос 'GET/films/search?query = {} & by = {} ", query, titleOrDirector);
        return filmService.getSearchFilms(query,titleOrDirector);
    }
}
