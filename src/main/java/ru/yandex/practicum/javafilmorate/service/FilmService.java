package ru.yandex.practicum.javafilmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.dao.*;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
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

    private final FilmDirectorDao filmDirectorDao;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;
    private final MpaDao mpaDao;
    private final UserStorage userStorage;


    public FilmService(@Qualifier("filmStorageDb") FilmStorage filmStorage
            , @Qualifier("userStorageDb") UserStorage userStorage
            , LikeDao likeDao
            , FilmGenreDao filmGenreDao
            , FilmDirectorDao filmDirectorDao
            , GenreDao genreDao
            , MpaDao mpaDao
            , DirectorDao directorDao) {
        this.filmStorage = filmStorage;
        this.likeDao = likeDao;
        this.filmGenreDao = filmGenreDao;
        this.filmDirectorDao = filmDirectorDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.directorDao = directorDao;
        this.userStorage = userStorage;
    }

    public Film addNewFilmToStorage(Film film) {
        filmStorage.addFilm(film);
        if (film.getGenres() != null) {
            filmGenreDao.insertFilmGenre(film);
        }
        if (film.getDirectors() != null) {
            filmDirectorDao.insertFilmDirector(film);
        }
        return film;
    }

    public Film updateFilmInStorage(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilmsFromStorage() {

        List<Film> films = filmStorage.getAllFilms();
        List<FilmGenre> filmGenres = filmGenreDao.getAllFilmGenres();
        List<FilmDirector> filmDirectors = filmDirectorDao.getAllFilmDirectors();

        Map<Long, Genre> genres = genreDao.getAll()
                .stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre));

        Map<Integer, Mpa> mpaList = mpaDao.getAll()
                .stream()
                .collect(Collectors.toMap(Mpa::getId, thisMpa -> thisMpa));

        Map<Long, Director> directorsList = directorDao.getAllDirectors()
                .stream()
                .collect(Collectors.toMap(Director::getId, director -> director));

        Map<Long, List<Genre>> mappedGenres = new HashMap<>();
        for (FilmGenre filmGenre : filmGenres) {
            if (!mappedGenres.containsKey(filmGenre.getFilmId())) {
                mappedGenres.put(filmGenre.getFilmId(), new ArrayList<>());
            }
            mappedGenres.get(filmGenre.getFilmId()).add(genres.get(filmGenre.getGenreId()));
        }

        Map<Long, List<Director>> mappedDirectors = new HashMap<>();
        for (FilmDirector filmDirector : filmDirectors) {
            if (!mappedDirectors.containsKey(filmDirector.getFilmId())) {
                mappedDirectors.put(filmDirector.getFilmId(), new ArrayList<>());
            }
            mappedDirectors.get(filmDirector.getFilmId()).add(directorsList.get(filmDirector.getDirectorId()));
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
            film.setDirectors(mappedDirectors.getOrDefault(film.getId(), new ArrayList<>()));
        });
        return films;
    }

    public Film getFilmFromStorage(long id) {
        Film film = filmStorage.getFilm(id);
        film.setLikes(likeDao.getFilmLikes(id));
        film.setGenres(filmGenreDao.getFilmGenre(id));
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        film.setDirectors(filmDirectorDao.getFilmDirector(id));
        return film;
    }

    //build
    public Film deleteFilmFromStorage(Long id) {
        return filmStorage.removeFilm(id);
    }

    public void likeFilmInStorage(long id, long userId) {
        filmStorage.getFilm(id);
        userStorage.getUser(userId);
        likeDao.putLike(id, userId);
    }

    public void deleteLikeFilmInStorage(long id, long userId) {
        likeDao.deleteLike(id, userId);
    }

    public List<Film> getDirectorFilms(long id, String sortBy) {
        if (directorDao.getDirectorById(id) == null) {
            throw new EntityDoesNotExistException("Не найден режиссер с id " + id);
        }
        List<Film> filmList = filmStorage.getDirectorFilms(id, sortBy);
        setForFilms(filmList);
        return filmList;
    }

    public List<Film> getMostPopularsFilmsByGenreByYear(int count, Long genreId, Integer year) {
        List<Film> films;
        if (genreId != null && year != null) {
            if (genreDao.getGenreById(genreId) == null) {
                throw new EntityDoesNotExistException(String.format(
                        "Жанр с идентификатором %d не найден.", genreId));
            }
            films = filmStorage.getMostPopularsFilmsByGenreByYear(count, genreId, year);
            setForFilms(films);
        } else if (genreId == null && year != null) {
            films = filmStorage.getMostPopularsFilmsByYear(count, year);
            setForFilms(films);
        } else if (year == null && genreId != null) {
            if (genreDao.getGenreById(genreId) == null) {
                throw new EntityDoesNotExistException(String.format(
                        "Жанр с идентификатором %d не найден.", genreId));
            }
            films = filmStorage.getMostPopularsFilmsByGenre(count, genreId);
            setForFilms(films);
        } else {
            films = filmStorage.getMostLikedFilms(count);
            setForFilms(films);
        }
        return films;
    }

    private void setForFilms(List<Film> films) {
        films.forEach((film) -> {
            film.setLikes(likeDao.getFilmLikes(film.getId()));
            film.setGenres(filmGenreDao.getFilmGenre(film.getId()));
            film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
            film.setDirectors(filmDirectorDao.getFilmDirector(film.getId()));
        });
    }

    public List<Film> getSearchFilms(String substring, List<String> titleOrDirector) {
        List<Film> films;
        String by1 = titleOrDirector.get(0);
        String by2 = "by";
        if (titleOrDirector.size() > 1) {
            by2 = titleOrDirector.get(1);
        }
        if (by1.equals("title")) {
            if (by2.equals("director")) {
                log.info("Поиск '{}' по названиям фильмов и режиссерам", substring);
                films = filmStorage.getSearchFilmsByTitleAndDirector(substring);
            } else {
                log.info("Поиск '{}' по названию фильма", substring);
                films = filmStorage.getSearchFilmsByTitle(substring);
            }
            setForFilms(films);
            return films;
        } else if (by2.equals("title")) {
            log.info("Поиск '{}' по названиям фильмов и режиссерам", substring);
            films = filmStorage.getSearchFilmsByTitleAndDirector(substring);
            setForFilms(films);
            return films;
        } else {
            log.info("Поиск '{}' по режиссеру", substring);
            films = filmStorage.getSearchFilmsByDirector(substring);
            setForFilms(films);
            return films;
        }
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        if (userStorage.getUser(userId) == null || userStorage.getUser(friendId) == null) {
            throw new EntityDoesNotExistException("Не найден пользователь с одним из данных id" + userId + friendId);
        }
        List<Film> films = filmStorage.getCommonFilms(userId);
        films.retainAll(filmStorage.getCommonFilms(friendId));
        setForFilms(films);
        return films;
    }
}