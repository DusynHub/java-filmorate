package ru.yandex.practicum.javafilmorate.storage;

import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.model.FilmSort;

import java.util.List;


public interface FilmStorage {

     Film addFilm(Film film);

     Film getFilm(Long id);

     Film removeFilm(Long id);

     Film updateFilm(Film film);

     List<Film> getAllFilms();
     boolean doesFilmExist(long id);

     public List<Film> getMostLikedFilms(int limit);
     public List<Film> getDirectorFilms(long id, String sortBy);

     public List<Film> getMostPopularsFilmsByGenreByYear(int count, long genreId, int year);

     public List<Film> getMostPopularsFilmsByGenre(int count, long genreId);

     public List<Film> getMostPopularsFilmsByYear(int count, int year);

     List<Film> getSearchFilmsByTitleAndDirector(String substring);

     List<Film> getSearchFilmsByTitle(String substring);

     List<Film> getSearchFilmsByDirector(String substring);
}