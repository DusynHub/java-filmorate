package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Like {

    private long filmId;
    private long userId;

    public Like(long filmId, long userId) {
        this.filmId = filmId;
        this.userId = userId;
    }
}
