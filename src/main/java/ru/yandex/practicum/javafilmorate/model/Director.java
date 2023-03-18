package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Director {
    long id;
    @NotNull
    @NotBlank
    String name;

    public static Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Director(id,name);
    }
}
