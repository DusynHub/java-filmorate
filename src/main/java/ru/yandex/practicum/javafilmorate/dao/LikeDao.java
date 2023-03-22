package ru.yandex.practicum.javafilmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.Like;
import ru.yandex.practicum.javafilmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<User> getFilmLikes(long id){
        String sql =    "SELECT  UF.id, UF.email, UF.login, UF.name, UF.birthday " +
                        "FROM likes l " +
                        "LEFT JOIN USERS UF on l.USER_ID = UF.ID " +
                        "WHERE film_id = ? ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> User.makeUser(rs), id);
    }

    public void putLike(long filmId, long userId){
        String sql =    "MERGE INTO LIKES l USING (VALUES (?,?)) s(filmId, userId) \n" +
                        "ON l.FILM_ID = S.filmId AND l.USER_ID = S.userId \n" +
                        "WHEN NOT MATCHED THEN INSERT VALUES ( S.filmId, S.userId) ";
        try{
            jdbcTemplate.update(sql, filmId, userId);
        } catch(DataIntegrityViolationException e) {
            log.debug("Фильм с id = {} или Пользователь с id = {} не найден.", filmId, userId);
            throw new EntityDoesNotExistException(
                    String.format("Фильм с id = %d или Пользователь с id = %d не найден."
                                                                                , filmId, userId));
        }

        String sql2 = "UPDATE FILM\n" +
                      "SET LIKES_AMOUNT = LIKES_AMOUNT + 1\n" +
                      "WHERE ID = ?";
        jdbcTemplate.update(sql2, filmId);
    }

    public void deleteLike(long filmId, long userId){
        if(userId < 0){
            log.debug("Пользователь с отрицательным id {} не может существовать.", userId);
            throw new EntityDoesNotExistException(
                    String.format("Пользователь с отрицательным id %d не может существовать.", userId));
        }

        String sql = "DELETE FROM LIKES  " +
                      "WHERE film_Id = ? AND user_Id = ?";
        try{
            jdbcTemplate.update(sql, filmId, userId);
        } catch(DataIntegrityViolationException e) {
            log.debug("Фильм с id = {} или Пользователь с id = {} не найден.", filmId, userId);
            throw new EntityDoesNotExistException(
                    String.format("Фильм с id = %d или Пользователь с id = %d не найден."
                                                                                , filmId, userId));
        }

        String sql2 = "UPDATE FILM\n" +
                      "SET LIKES_AMOUNT = LIKES_AMOUNT - 1\n" +
                      "WHERE ID = ?";
        jdbcTemplate.update(sql2, filmId);
    }

    public List<Like> getAllLikes(){
        String sql =    "SELECT l.film_id, l.user_id " +
                        "FROM likes l ";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> makeLike(rs));
    }

    public Like makeLike(ResultSet resultSet) throws SQLException {
        long filmId = resultSet.getLong("film_id");
        long userId = resultSet.getLong("user_id");

        return Like.builder().filmId(filmId).userId(userId).build();
    }

    public List<Like> getUserLikesById(long id){
        String sql =    "SELECT l.film_id, l.user_id \n" +
                        "FROM likes l \n" +
                        "WHERE user_id = ? \n";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeLike(rs), id);
    }

    public List<Like> getFirstUserWithSameLikedFilms(List<Long> filmsIdLikedByUser, long id){

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmsIdLikedByUser", filmsIdLikedByUser);
        parameters.addValue("ids", id);

        String sql =    "SELECT l.USER_ID, COUNT(l.FILM_ID) AS FILM_ID \n" +
                        "FROM likes l \n" +
                        "WHERE l.USER_ID NOT IN (:ids) AND l.FILM_ID IN (:filmsIdLikedByUser) \n" +
                        "GROUP BY l.USER_ID \n" +
                        "ORDER BY COUNT(l.FILM_ID) DESC \n" +
                        "LIMIT 1 \n";

        return namedParameterJdbcTemplate.query(sql,parameters ,(rs, rowNum) -> makeLike(rs));
    }
}
