package ru.yandex.practicum.javafilmorate.storage;

import ru.yandex.practicum.javafilmorate.model.Film;

import java.util.List;


public interface FilmStorage {

    Film addFilm(Film film);

    Film getFilm(Long id);

    Film removeFilm(Long id);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    boolean doesFilmExist(long id);

    public List<Film> getMostLikedFilms(int limit);

    public List<Film> getMostPopularsFilmsByGenreByYear(int count, long genreId, int year);

    public List<Film> getMostPopularsFilmsByGenre(int count, long genreId);

    public List<Film> getMostPopularsFilmsByYear(int count, int year);

}
