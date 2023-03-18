package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Director {
    long id;
    String name;

    public static Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Director(id,name);
    }
}
