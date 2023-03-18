package ru.yandex.practicum.javafilmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public List<Director> getFilmDirector(long id){
        String sql = "SELECT d.id, d.name " +
                "FROM FILM_DIRECTOR fd " +
                "LEFT JOIN DIRECTOR d ON  fd.DIRECTOR_ID = d.id " +
                "WHERE fd.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Director.makeDirector(rs), id);
    }

    public Film insertFilmDirector(Film film) {
        String sql = "INSERT INTO FILM_DIRECTOR(FILM_ID, DIRECTOR_ID)  " +
                "VALUES(?,?)";

        List<Director> uniqDirectors = film.getDirectors().stream().distinct().collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, uniqDirectors.get(i).getId());
                    }
                    public int getBatchSize() {
                        return uniqDirectors.size();
                    }
                });
        film.setDirectors(uniqDirectors);
        return film;
    }

    public void deleteAllFilmDirectorsByFilmId(long filmId ){

        String sql = "DELETE FROM FILM_DIRECTOR " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql,filmId);
    }

    public List<FilmDirector> getAllFilmDirectors() {
        String sql = "SELECT fg.film_id, fg.DIRECTOR_ID " +
                "FROM FILM_DIRECTOR fg";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> makeFilmDirector(rs));
    }

    public FilmDirector makeFilmDirector(ResultSet resultSet) throws SQLException {
        long filmId = resultSet.getLong("film_id");
        long directorId = resultSet.getLong("directorId");

        return FilmDirector.builder().filmId(filmId).directorId(directorId).build();
    }

}
