package ru.yandex.practicum.javafilmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.javafilmorate.model.Feed;
import ru.yandex.practicum.javafilmorate.storage.FeedDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FeedDaoImpl implements FeedDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Feed> getFeed(Long userId, Integer limit) {
        String sqlQuery = "SELECT * FROM FEED_LIST where USER_ID = ? ORDER BY FEED_DATE ASC LIMIT ?";
        return jdbcTemplate.queryForStream(sqlQuery, (rs, rowNum) ->
                        Feed.builder()
                                .eventId(rs.getLong("EVENT_ID"))
                                .userId(rs.getLong("USER_ID"))
                                .eventType(rs.getString("EVENT_TYPE"))
                                .entityId(rs.getLong("ENTITY_ID"))
                                .operationType(rs.getString("OPERATION_TYPE"))
                                .feedDate(rs.getTimestamp("FEED_DATE").getTime())
                                .build(), userId,
                limit > 0 ? limit : 0
        ).collect(Collectors.toList());
    }

    @Override
    public void addFeed(Feed feed) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FEED_LIST")
                .usingGeneratedKeyColumns("EVENT_ID");

        simpleJdbcInsert.execute(feedToMap(feed));
    }

    @Override
    public Feed findFeedByEntityId(Long reviewId) {
        final String sql = "SELECT * FROM FEED_LIST where ENTITY_ID = ? limit 1";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Feed>() {
            @Override
            public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Feed.builder()
                        .eventId(rs.getLong("EVENT_ID"))
                        .userId(rs.getLong("USER_ID"))
                        .feedDate(rs.getTimestamp("FEED_DATE").getTime())
                        .eventType(rs.getString("EVENT_TYPE"))
                        .operationType(rs.getString("OPERATION_TYPE"))
                        .entityId(rs.getLong("ENTITY_ID"))
                        .build();
            }
        }, reviewId);
    }

    public Map<String, Object> feedToMap(Feed feed) {
        Map<String, Object> values = new HashMap<>();
        values.put("USER_ID", feed.getUserId());
        values.put("EVENT_TYPE", feed.getEventType());
        values.put("OPERATION_TYPE", feed.getOperationType());
        values.put("FEED_DATE", Timestamp.valueOf(LocalDateTime.now()));
        values.put("ENTITY_ID", feed.getEntityId());
        return values;
    }
}
