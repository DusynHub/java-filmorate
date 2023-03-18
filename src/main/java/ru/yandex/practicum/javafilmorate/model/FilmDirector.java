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

    public FilmDirector(long directorId, long filmId) {
        this.directorId = directorId;
        this.filmId = filmId;
    }
}
