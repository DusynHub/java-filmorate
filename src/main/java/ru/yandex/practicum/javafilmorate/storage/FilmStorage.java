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

     List<Film> getSearchFilmsByTitleAndDirector(String substring);

     List<Film> getSearchFilmsByTitle(String substring);

     List<Film> getSearchFilmsByDirector(String substring);
}
