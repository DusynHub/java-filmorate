package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Friendship {

    private long id;
    private long friend1Id;
    private long friend2Id;

    public Friendship(long id, long friend1Id, long friend2Id) {
        this.id = id;
        this.friend1Id = friend1Id;
        this.friend2Id = friend2Id;
    }
}
