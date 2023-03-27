package ru.yandex.practicum.javafilmorate.exceptions;

public class EntityDoesNotExistException extends RuntimeException {

    public EntityDoesNotExistException(String message) {
        super(message);
    }
}
