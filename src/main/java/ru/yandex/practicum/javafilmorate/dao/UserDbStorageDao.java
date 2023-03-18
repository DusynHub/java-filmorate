package ru.yandex.practicum.javafilmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.User;
import ru.yandex.practicum.javafilmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Qualifier("userStorageDb")
@RequiredArgsConstructor
public class UserDbStorageDao implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sqlQuery = "insert into USERS ( EMAIL, login, name, birthday)" +
                          "values (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();


        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf( user.getBirthday()));
            return stmt;
        }, keyHolder);
       long idKey = Objects.requireNonNull(keyHolder.getKey()).longValue();
       user.setId(idKey);
       return user;
    }

    @Override
    public User getUser(Long id) {
        String sql = "SELECT id, email,login,name,birthday " +
                     "FROM USERS " +
                     "WHERE id = ? LIMIT 1";
        try {
            User user = jdbcTemplate.queryForObject(sql,
                    (ResultSet rs, int rowNum) -> User.makeUser(rs),
                    id);
            if(user != null){
                log.info("Найден пользователь: c id = {} именем = {}", user.getId(), user.getName());
                user.setFriends(getUserFriends(id));
            }
            return user;
        } catch(EmptyResultDataAccessException e){
            log.debug("Пользователь с идентификатором {} не найден.", id);
            throw new EntityDoesNotExistException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
    }

    @Override
    public User removeUser(Long id) {
        User user = getUser(id);
        jdbcTemplate.update("DELETE FROM FRIENDSHIP WHERE FRIEND2_ID = ? || FRIEND2_ID = ?", id, id);
        jdbcTemplate.update("DELETE FROM FEED_LIST WHERE USER_ID = ?", id);
        jdbcTemplate.update("DELETE FROM LIKES WHERE USER_ID = ?", id);
        jdbcTemplate.update("DELETE FROM REVIEWS WHERE USER_ID = ?", id);
        jdbcTemplate.update("DELETE FROM USERS WHERE ID = ?", id);
        return user;
    }

    @Override
    public User updateUser(User user) {

        String sqlQuery = "UPDATE USERS " +
    "                      SET   email = ?," +
                                "login = ?," +
                                "name = ?," +
                                "birthday = ?" +
                           "WHERE id = ?";
        int updatedRows= jdbcTemplate.update(sqlQuery
                                                    , user.getEmail()
                                                    , user.getLogin()
                                                    , user.getName()
                                                    , Date.valueOf( user.getBirthday())
                                                    , user.getId());
        if(updatedRows == 0){
            log.debug("Пользователь с идентификатором {} не найден.", user.getId());
            throw new EntityDoesNotExistException(
                    String.format("Пользователь с идентификатором %d не найден.", user.getId()));
        } else{
            return user;
        }
    }

    @Override
    public List<User> getAllUsers() {

        String sql = "SELECT id, email, login, name, birthday\n" +
                     "FROM USERS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> User.makeUser(rs));
    }


    public List<User> getUserFriends(long id){
        String sql =    "SELECT  UF.id, UF.email, UF.login, UF.name, UF.birthday " +
                "FROM FRIENDSHIP f " +
                "LEFT JOIN USERS UF on f.FRIEND2_ID = UF.ID " +
                "WHERE FRIEND1_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> User.makeUser(rs), id);
    }
}
