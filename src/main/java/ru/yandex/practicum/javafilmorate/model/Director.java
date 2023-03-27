package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@ToString
@Builder
public class Director {
    private long id;
    @NotNull
    @NotBlank
    private String name;

    public static Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return builder()
                .id(id)
                .name(name)
                .build();
    }
}

