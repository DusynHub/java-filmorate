package ru.yandex.practicum.javafilmorate.model;

import lombok.*;
import ru.yandex.practicum.javafilmorate.util.StringWithoutSpaceSymbol;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class User {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotNull(message = "Email не может быть не задан")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректным")
    private String email;
    @NotNull(message = "Логин не может быть не задан")
    @NotBlank(message = "Логин не может быть пустым")
    @StringWithoutSpaceSymbol
    private String login;
    private String name;
    @Past(message = "День рождения не может быть в будущем")
    private LocalDate birthday;
    private List<User> friends;

    public static User makeUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }
}
