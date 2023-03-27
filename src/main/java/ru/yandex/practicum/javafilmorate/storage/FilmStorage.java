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

    List<Film> getMostLikedFilms(int limit);

    List<Film> getDirectorFilms(long id, String sortBy);

    List<Film> getMostPopularsFilmsByGenreByYear(int count, long genreId, int year);

    List<Film> getMostPopularsFilmsByGenre(int count, long genreId);

    List<Film> getMostPopularsFilmsByYear(int count, int year);

    List<Film> getCommonFilms(long userId);

    List<Film> getSearchFilmsByTitleAndDirector(String substring);

    List<Film> getSearchFilmsByTitle(String substring);

    List<Film> getSearchFilmsByDirector(String substring);
}