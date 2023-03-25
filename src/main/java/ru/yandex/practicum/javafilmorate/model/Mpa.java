package ru.yandex.practicum.javafilmorate.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class Mpa {

    private int id;

    private String name;

    public Mpa(int id) {
        this.id = id;
    }
}
