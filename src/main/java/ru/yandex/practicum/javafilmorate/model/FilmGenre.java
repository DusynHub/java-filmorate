package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class FilmGenre {

    private long genreId;
    private long filmId;

    public FilmGenre(long genreId, long filmId) {
        this.genreId = genreId;
        this.filmId = filmId;
    }
}
