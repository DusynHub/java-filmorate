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
import ru.yandex.practicum.javafilmorate.model.Genre;
import ru.yandex.practicum.javafilmorate.model.Mpa;
import ru.yandex.practicum.javafilmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Qualifier("filmStorageDb")
@RequiredArgsConstructor
public class FilmDbStorageDao implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

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

    @Override
    public Film removeFilm(Long id) {
        Film film = getFilm(id);
        String sql = "DELETE FROM FILM \n" +
                     "WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM " +
                     "SET name = ?, " +
                         "description = ?, " +
                         "release_date = ?, " +
                         "duration = ?, " +
                         "mpa = ?, "+
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
        if(updatedRows == 0){
            log.debug("Фильм с идентификатором {} не найден.", film.getId());
            throw new EntityDoesNotExistException(
                    String.format("Фильм с идентификатором %d не найден.", film.getId()));
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
        return film != null ;
    }

    @Override
    public List<Film> getMostLikedFilms(int limit) {
        String sql = String.format(
                "SELECT id, name, description, release_date, duration, mpa, rate, LIKES_AMOUNT \n" +
                "FROM FILM\n" +
                "ORDER BY LIKES_AMOUNT DESC\n" +
                "LIMIT %d", limit
                );
        return jdbcTemplate.query(sql, (rs, rowNum) ->  Film.makeFilm(rs));
    }

    @Override
    public List<Film> getDirectorFilms(long id, String sortBy) {
        return null;
    }

    /*@Override
    public List<Film> getSearchFilmsByTitleAndDirector(String substring) {
        //String sub = "'%" + substring + "%'";
        //System.out.println(sub);
        String sql = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA, F.RATE, F.LIKES_AMOUNT \n" +
                "FROM FILM AS F\n" +
                "LEFT JOIN FILM_DIRECTOR AS FD ON F.ID = FD.FILM_ID\n" +
                "LEFT JOIN DIRECTOR AS D ON FD.DIRECTOR_ID = D.ID\n" +
                "WHERE (F.NAME LIKE ? AND D.NAME LIKE ?)\n" +
                "ORDER BY F.LIKES_AMOUNT DESC";
        System.out.println(sql);
        return jdbcTemplate.query(sql, (rs, rowNum) ->  Film.makeFilm(rs), "%" + substring + "%", "%" + substring + "%");
    }

    @Override
    public List<Film> getSearchFilmsByTitle(String substring) {
        //String sub = "'%" + substring + "%'";
        //System.out.println(sub);
        String sql ="SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA, F.RATE, F.LIKES_AMOUNT \n" +
                "FROM FILM AS F\n" +
                "WHERE F.NAME LIKE ?\n" +
                "ORDER BY F.LIKES_AMOUNT DESC";

        System.out.println(sql);
        return jdbcTemplate.query(sql, (rs, rowNum) ->  Film.makeFilm(rs), "%" + substring + "%" );
    }

    @Override
    public List<Film> getSearchFilmsByDirector(String substring) {
        //String sub = "'%" + substring + "%'";
        //System.out.println(sub);
        String sql = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA, F.RATE, F.LIKES_AMOUNT \n" +
                "FROM FILM AS F\n" +
                "LEFT JOIN FILM_DIRECTOR AS FD ON F.ID = FD.FILM_ID\n" +
                "LEFT JOIN DIRECTOR AS D ON FD.DIRECTOR_ID = D.ID\n" +
                "WHERE D.NAME LIKE ?\n" +
                "ORDER BY F.LIKES_AMOUNT DESC";
        System.out.println(sql);
        return jdbcTemplate.query(sql, (rs, rowNum) ->  Film.makeFilm(rs), "%" + substring + "%");
        //return jdbcTemplate.query(sql, this::mapRowToFilm);
    }*/

    @Override
    public List<Film> getSearchFilmsByTitleAndDirector(String substring) {
        String sub = "'%" + substring + "%'";
        System.out.println(sub);
        String sql = String.format("SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA, F.RATE, F.LIKES_AMOUNT \n" +
                "FROM FILM AS F\n" +
                "LEFT JOIN FILM_DIRECTOR AS FD ON F.ID = FD.FILM_ID\n" +
                "LEFT JOIN DIRECTOR AS D ON FD.DIRECTOR_ID = D.ID\n" +
                "WHERE (F.NAME ILIKE %s AND D.NAME ILIKE %s)\n" +
                "ORDER BY F.LIKES_AMOUNT DESC", sub, sub);
        System.out.println(sql);
        return jdbcTemplate.query(sql, (rs, rowNum) ->  Film.makeFilm(rs));
    }

    /*@Override
    public List<Film> getSearchFilmsByTitle(String substring) {
        String sub = "'%" + substring + "%'";
        System.out.println(sub);
        String sql = String.format("SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA, F.RATE, F.LIKES_AMOUNT \n" +
                "FROM FILM AS F\n" +
                "WHERE F.NAME LIKE %s\n" +
                "ORDER BY F.LIKES_AMOUNT DESC", sub);
        System.out.println(sql);
        return jdbcTemplate.query(sql, (rs, rowNum) ->  Film.makeFilm(rs));
    }*/

    @Override
    public List<Film> getSearchFilmsByTitle(String substring) {
        String sub = "'%" + substring + "%'";
        System.out.println(sub);
        String sql = String.format("SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA, F.RATE, F.LIKES_AMOUNT \n" +
                "FROM FILM AS F\n" +
                "WHERE F.NAME ILIKE %s\n" +
                "ORDER BY F.LIKES_AMOUNT DESC", sub);
        System.out.println(sql);
        return jdbcTemplate.query(sql, (rs, rowNum) ->  Film.makeFilm(rs));
    }

    @Override
    public List<Film> getSearchFilmsByDirector(String substring) {
        String sub = "'%" + substring + "%'";
        System.out.println(sub);
        String sql = String.format("SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA, F.RATE, F.LIKES_AMOUNT \n" +
                "FROM FILM AS F\n" +
                "LEFT JOIN FILM_DIRECTOR AS FD ON F.ID = FD.FILM_ID\n" +
                "LEFT JOIN DIRECTOR AS D ON FD.DIRECTOR_ID = D.ID\n" +
                "WHERE D.NAME ILIKE %s\n" +
                "ORDER BY F.LIKES_AMOUNT DESC", sub);
        System.out.println(sql);
        return jdbcTemplate.query(sql, (rs, rowNum) ->  Film.makeFilm(rs));
        //return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    /*private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(rs.getLong("id")
                , rs.getString("name")
                , rs.getString("description")
                , rs.getDate("release_date").toLocalDate()
                , rs.getInt("duration")
                , new Mpa(rs.getInt("mpa"))
                , rs.getInt("rate")
                , rs.getInt("LIKES_AMOUNT"));
        return film;
    }*/


}

