package ru.yandex.practicum.javafilmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.Genre;

import java.sql.ResultSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAll() {
        String sql = "SELECT id, name " +
                "FROM Genre";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Genre.makeGenre(rs));
    }

    public Genre getGenreById(long id) {
        String sql = "SELECT id, name " +
                "FROM Genre " +
                "WHERE id = ?";

        try {
            Genre mpa = jdbcTemplate.queryForObject(sql,
                    (ResultSet rs, int rowNum) -> Genre.makeGenre(rs),
                    id);
            if (mpa != null) {
                log.info("Найден Жанр: c id = {} названием = {}", mpa.getId(), mpa.getName());
            }
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Жанр с идентификатором {} не найден.", id);
            throw new EntityDoesNotExistException(String.format(
                    "Жанр с идентификатором %d не найден.", id));
        }
    }
}
