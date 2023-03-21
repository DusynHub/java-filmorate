package ru.yandex.practicum.javafilmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.dao.*;
import ru.yandex.practicum.javafilmorate.model.*;
import ru.yandex.practicum.javafilmorate.storage.FilmStorage;
import ru.yandex.practicum.javafilmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeDao likeDao;
    private final FilmGenreDao filmGenreDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final UserStorage userStorage;


    public FilmService(@Qualifier("filmStorageDb") FilmStorage filmStorage
                        , @Qualifier("userStorageDb") UserStorage userStorage
                        , LikeDao likeDao
                        , FilmGenreDao filmGenreDao
                        , GenreDao genreDao
                        , MpaDao mpaDao) {
        this.filmStorage = filmStorage;
        this.likeDao = likeDao;
        this.filmGenreDao = filmGenreDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.userStorage = userStorage;
    }

    public Film addNewFilmToStorage(Film film) {
        filmStorage.addFilm(film);
        if(film.getGenres() == null || film.getGenres().isEmpty()){
            return film;
        }
        filmGenreDao.insertFilmGenre(film);
        return film;
    }

    public Film updateFilmInStorage(Film film) {
        filmStorage.updateFilm(film);
        filmGenreDao.deleteAllFilmGenresByFilmId(film.getId());
        if(film.getGenres() == null || film.getGenres().isEmpty()){
            return film;
        }
        System.out.println(film);
        return filmGenreDao.insertFilmGenre(film);
    }

    public List<Film> getAllFilmsFromStorage() {

        List<Film> films = filmStorage.getAllFilms();
        List<FilmGenre> filmGenres = filmGenreDao.getAllFilmGenres();
        Map<Long, Genre> genres = genreDao.getAll()
                                            .stream()
                                            .collect(Collectors.toMap(Genre::getId, genre -> genre));

        Map<Integer, Mpa> mpaList = mpaDao.getAll()
                                            .stream()
                                            .collect(Collectors.toMap(Mpa::getId, thisMpa -> thisMpa));


        Map<Long, List<Genre>> mappedGenres = new HashMap<>();
        for (FilmGenre filmGenre : filmGenres) {
            if (!mappedGenres.containsKey(filmGenre.getFilmId())) {
                mappedGenres.put(filmGenre.getFilmId(), new ArrayList<>());
            }
            mappedGenres.get(filmGenre.getFilmId()).add(genres.get(filmGenre.getGenreId()));
        }

        List<Like> allLikes = likeDao.getAllLikes();
        Map<Long, User> allUsers = userStorage.getAllUsers()
                                                .stream()
                                                .collect(Collectors.toMap(User::getId, user -> user));

        Map<Long, List<User>> mappedUsers = new HashMap<>();

        for (Like like : allLikes) {
            if (!mappedUsers.containsKey(like.getFilmId())) {
                mappedUsers.put(like.getFilmId(), new ArrayList<>());
            }
            mappedUsers.get(like.getFilmId()).add(allUsers.get(like.getUserId()));
        }

        films.forEach(film -> {
            film.setGenres(mappedGenres.getOrDefault(film.getId(), new ArrayList<>()));
            film.setMpa(mpaList.get(film.getMpa().getId()));
            film.setLikes(mappedUsers.getOrDefault(film.getId(), new ArrayList<>()));
        });
        return films;
    }

    public Film getFilmFromStorage(long id) {
        Film film = filmStorage.getFilm(id);
        film.setLikes(likeDao.getFilmLikes(id));
        film.setGenres(filmGenreDao.getFilmGenre(id));
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        return film;
    }

    public void likeFilmInStorage(long id, long userId) {
        likeDao.putLike(id, userId);
    }

    public void deleteLikeFilmInStorage(long id, long userId) {
        likeDao.deleteLike(id, userId);
    }

    public List<Film> getMostLikedFilmsFromStorage(int count) {
        List<Film> films = filmStorage.getMostLikedFilms(count);
        films.forEach((film) -> {
            film.setLikes(likeDao.getFilmLikes(film.getId()));
            film.setGenres(filmGenreDao.getFilmGenre(film.getId()));
            film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        });
        return films;
    }
}
