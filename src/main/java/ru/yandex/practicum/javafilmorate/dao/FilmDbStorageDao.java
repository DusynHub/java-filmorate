package ru.yandex.practicum.javafilmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.Director;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.model.FilmSort;
import ru.yandex.practicum.javafilmorate.model.Genre;
import ru.yandex.practicum.javafilmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Qualifier("filmStorageDb")
@RequiredArgsConstructor
public class FilmDbStorageDao implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;
    private final FilmDirectorDao filmDirectorDao;

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO FILM ( name, description, release_date, duration, mpa, rate, LIKES_AMOUNT) " +
                          "VALUES (?,?,?,?,?,?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, (int) film.getDuration().toSeconds());
                stmt.setInt(5, film.getMpa().getId());
                stmt.setInt(6, film.getRate());
                stmt.setInt(7, 0);
                return stmt;
            }, keyHolder);
            long idKey = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setId(idKey);
            return film;
    }

    @Override
    public Film getFilm(Long id) {
        String sql = "SELECT id, name, description, release_date, duration, mpa, rate, LIKES_AMOUNT\n" +
                     "FROM film " +
                     "WHERE id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sql,
                    (ResultSet rs, int rowNum) -> Film.makeFilm(rs),
                    id);
            assert film != null;
            log.info("Найден фильм: c id = {} названием = {}", film.getId(), film.getName());
            return film;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильм с идентификатором {} не найден.", id);
            throw new EntityDoesNotExistException(String.format("Фильм с идентификатором %d не найден.", id));
        }
    }

    // Проверка таблиц по названиям. При удалении фильма удаляем все его появления в других таблицах
    @Override
    public Film removeFilm(Long id) {
        Film film = getFilm(id);
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM FILM_DIRECTOR WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM LIKES WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM REVIEWS WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM FILM WHERE id = ?", id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM " +
                "SET name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa = ?, " +
                "rate = ? " +
                " WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sql
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , (int) film.getDuration().toSeconds()
                , film.getMpa().getId()
                , film.getRate()
                , film.getId());
        if (updatedRows == 0) {
            log.debug("Фильм с идентификатором {} не найден.", film.getId());
            throw new EntityDoesNotExistException(
                    String.format("Фильм с идентификатором %d не найден.", film.getId()));
        }
        if (film.getGenres() != null) {
            filmGenreDao.deleteAllFilmGenresByFilmId(film.getId());
            filmGenreDao.insertFilmGenre(film);
        } else {
            if (filmGenreDao.getFilmGenre(film.getId()) != null) {
                film.setGenres(filmGenreDao.getFilmGenre(film.getId()));
            }
        }
        filmDirectorDao.deleteAllFilmDirectorsByFilmId(film.getId());

        if (film.getDirectors() != null) {
            filmDirectorDao.insertFilmDirector(film);
        }

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT id, name, description, release_date, duration, mpa ,rate , LIKES_AMOUNT \n" +
                "FROM film ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Film.makeFilm(rs));
    }

    @Override
    public boolean doesFilmExist(long id) {
        Film film = getFilm(id);
        return film != null;
    }

    @Override
    public List<Film> getMostLikedFilms(int limit) {
        String sql = ("SELECT id, name, description, release_date, duration, mpa, rate, LIKES_AMOUNT \n" +
                        "FROM FILM\n" +
                        "ORDER BY LIKES_AMOUNT DESC\n" +
                        "LIMIT ?"
        );
        return jdbcTemplate.query(sql, (rs, rowNum) -> Film.makeFilm(rs),limit);
    }

    @Override
    public List<Film> getMostPopularsFilmsByGenreByYear(int count, long genreId, int year) {
        String sql = ("SELECT * " +
                        "FROM film f " +
                        "INNER JOIN film_genre fg ON f.id = fg.film_id " +
                        "INNER JOIN genre g ON fg.genre_id = g.id " +
                        "WHERE g.id = ? AND EXTRACT(YEAR FROM CAST(release_date AS date)) = ? " +
                        "LIMIT ?"
        );
        return jdbcTemplate.query(sql, (rs, rowNum) -> Film.makeFilm(rs),genreId, year, count);
    }

    @Override
    public List<Film> getDirectorFilms(long id, String sortBy) {
        String sql;
        if (sortBy.equals("year")) {
            sql = "SELECT F.*, FD.DIRECTOR_ID " +
                    "FROM FILM_DIRECTOR FD " +
                    "JOIN FILM F on F.ID = FD.FILM_ID " +
                    "WHERE DIRECTOR_ID = ? " +
                    "GROUP BY f.ID, F.RELEASE_DATE " +
                    "ORDER BY F.RELEASE_DATE";
        } else if (sortBy.equals("likes")) {
            sql = "SELECT f.*, FD.DIRECTOR_ID " +
                    "FROM FILM_DIRECTOR FD " +
                    "JOIN FILM F on F.ID = FD.FILM_ID " +
                    "LEFT JOIN LIKES fl on F.ID = fl.film_id " +
                    "WHERE DIRECTOR_ID = ? " +
                    "GROUP BY f.ID, fl.film_id IN ( " +
                    "SELECT film_id " +
                    "FROM LIKES " +
                    ") " +
                    "ORDER BY COUNT(fl.film_id) DESC";
        } else {
            throw new RuntimeException("Ошибка ввода");
        }
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> Film.makeFilm(rs), id);
    }

    @Override
    public List<Film> getMostPopularsFilmsByGenre(int count, long genreId) {
        String sql = ("SELECT * " +
                        "FROM film f " +
                        "INNER JOIN film_genre fg ON f.id = fg.film_id " +
                        "INNER JOIN genre g ON fg.genre_id = g.id " +
                        "WHERE g.id = ? " +
                        "LIMIT ?"
        );
        return jdbcTemplate.query(sql, (rs, rowNum) -> Film.makeFilm(rs),genreId,count);
    }

    @Override
    public List<Film> getMostPopularsFilmsByYear(int count, int year) {
        String sql = ("SELECT * " +
                        "FROM film f " +
                        "WHERE EXTRACT(YEAR FROM CAST(release_date AS date)) = ? " +
                        "LIMIT ?"
        );
        return jdbcTemplate.query(sql, (rs, rowNum) -> Film.makeFilm(rs), year,count);
    }
}