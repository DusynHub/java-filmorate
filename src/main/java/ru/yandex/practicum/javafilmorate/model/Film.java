package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

import ru.yandex.practicum.javafilmorate.util.DurationPositiveOrZero;
import ru.yandex.practicum.javafilmorate.util.FilmDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class Film  {

    @EqualsAndHashCode.Exclude
    private long id;
    @NotNull
    @NotBlank
    private String name;
    @Size(max = 200, message = "Длина описания не должна превышать 200 символов")
    private String description;
    @FilmDate
    private LocalDate releaseDate;
    @DurationPositiveOrZero
    private Duration duration;
    private List<User> likes;
    private int rate;
    private Mpa mpa;
    private List<Genre> genres;
    private int likesAmount;
    private List<Director> directors;

    public static Film makeFilm(ResultSet rs) throws SQLException {
        System.out.println(rs);
        long id = rs.getLong("id");
        System.out.println(id);
        String name = rs.getString("name");
        System.out.println(name);
        String description = rs.getString("description");
        System.out.println(description);
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        System.out.println(releaseDate);
        Duration duration = Duration.ofSeconds(rs.getInt("duration"));
        System.out.println(duration);
        Mpa mpa = new Mpa(rs.getInt("mpa"));
        System.out.println(mpa);
        int rate = rs.getInt("rate");
        System.out.println(rate);
        int likesAmount = rs.getInt("LIKES_AMOUNT");
        System.out.println(likesAmount);

        return builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .releaseDate(releaseDate)
                    .duration(duration)
                    .rate(rate)
                    .mpa(mpa)
                    .likesAmount(likesAmount)
                    .build();
    }
}
