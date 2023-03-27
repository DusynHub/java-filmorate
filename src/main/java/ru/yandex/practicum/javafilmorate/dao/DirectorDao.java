package ru.yandex.practicum.javafilmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Director> getAllDirectors() {
        String sql = "SELECT id, name " +
                "FROM DIRECTOR";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    public Director getDirectorById(long id) {
        String sql = "SELECT id, name " +
                "FROM Director " +
                "WHERE id = ?";

        try {
            Director director = jdbcTemplate.queryForObject(sql,
                    (ResultSet rs, int rowNum) -> makeDirector(rs),
                    id);
            if (director != null) {
                log.info("Найден режиссёр c id = {}, именем = {}", director.getId(),
                        director.getName());
            }
            return director;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Режиссёр с идентификатором {} не найден.", id);
            throw new EntityDoesNotExistException(String.format(
                    "Режиссёр с идентификатором %d не найден.", id));
        }
    }

    public Director createDirectorInDb(Director director) {
        String sqlQuery = "INSERT INTO DIRECTOR (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        long idKey = Objects.requireNonNull(keyHolder.getKey()).longValue();
        director.setId(idKey);
        return director;
    }

    public Director updateDirectorInDb(Director director) {
        String sql = "UPDATE DIRECTOR SET name = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (updatedRows == 0) {
            log.debug("Режиссёр с идентификатором {} не найден.", director.getId());
            throw new EntityDoesNotExistException(
                    String.format("Режиссёр с идентификатором %d не найден.", director.getId()));
        }
        return director;
    }

    public Director deleteDirectorFromDb(long id) {
        Director director = getDirectorById(id);
        jdbcTemplate.update("DELETE FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?", id);
        jdbcTemplate.update("DELETE FROM DIRECTOR WHERE id = ?", id);
        return director;
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return Director.builder().id(id).name(name).build();
    }
}
