package ru.yandex.practicum.javafilmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.Friendship;
import ru.yandex.practicum.javafilmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendShipDao {

    private final JdbcTemplate jdbcTemplate;

    public void addFriends(long friend1, long friend2) {
        String sql = "MERGE INTO FRIENDSHIP f USING (VALUES (?,?)) S(friend1, friend2)\n" +
                "ON f.FRIEND1_ID = S.friend1 AND f.FRIEND2_ID = S.friend2 \n" +
                "WHEN NOT MATCHED THEN INSERT VALUES ( S.friend1, S.friend2)";
        try {
            jdbcTemplate.update(sql, friend1, friend2);
        } catch (DataIntegrityViolationException e) {
            log.debug("Пользователь с идентификатором {} или {} не найден.", friend1, friend2);
            throw new EntityDoesNotExistException(
                    String.format("Пользователь с идентификатором %d или %d не найден.", friend1, friend2));
        }
    }

    public void deleteFriends(long friend1, long friend2) {
        String sql = "MERGE INTO FRIENDSHIP f USING (VALUES (?,?)) S(friend1, friend2)\n" +
                "ON f.FRIEND1_ID = S.friend1 AND f.FRIEND2_ID = S.friend2 \n" +
                "WHEN  MATCHED THEN DELETE";

        jdbcTemplate.update(sql, friend1, friend2);
    }

    public List<User> getUserFriends(long id) {
        try {
            jdbcTemplate.queryForObject("SELECT id, email,login,name,birthday FROM USERS " +
                            "WHERE id = ? LIMIT 1",
                    (ResultSet rs, int rowNum) -> User.makeUser(rs),
                    id);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Пользователь с идентификатором {} не найден.", id);
            throw new EntityDoesNotExistException(String.format("Пользователь с идентификатором %d не найден.", id));
        }

        String sql = "SELECT  UF.id, UF.email, UF.login, UF.name, UF.birthday " +
                "FROM FRIENDSHIP f " +
                "LEFT JOIN USERS UF on f.FRIEND2_ID = UF.ID " +
                "WHERE FRIEND1_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> User.makeUser(rs), id);
    }

    public List<User> getCommonFriends(long friend1, long friend2) {

        String sql2 = "SELECT UF.ID, UF.EMAIL, UF.LOGIN, UF.NAME, UF.BIRTHDAY \n" +
                "FROM FRIENDSHIP F\n" +
                "INNER JOIN USERS UF ON F.FRIEND2_ID = UF.ID\n" +
                "WHERE FRIEND1_ID = ? AND FRIEND2_ID IN (\n" +
                "        SELECT FRIEND2_ID\n" +
                "        FROM FRIENDSHIP\n" +
                "        WHERE FRIEND1_ID = ?\n" +
                "     )";

        return jdbcTemplate.query(sql2, (rs, rowNum) -> User.makeUser(rs), friend1, friend2);
    }


    public List<Friendship> getAllFriendship() {

        String sql2 = "SELECT f.friend1_id, f.friend2_id \n" +
                "FROM FRIENDSHIP f \n";

        return jdbcTemplate.query(sql2, (rs, rowNum) -> makeFriendship(rs));
    }

    public Friendship makeFriendship(ResultSet rs) throws SQLException {
        long friend1_id = rs.getLong("friend1_id");
        long friend2_id = rs.getLong("friend2_id");

        return Friendship.builder().friend1Id(friend1_id).friend2Id(friend2_id).build();
    }
}
