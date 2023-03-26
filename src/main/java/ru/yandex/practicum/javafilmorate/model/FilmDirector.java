package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class FilmDirector {

    private long directorId;
    private long filmId;
}
