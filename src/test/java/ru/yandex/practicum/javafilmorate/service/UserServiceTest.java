package ru.yandex.practicum.javafilmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.model.Mpa;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;

    @Test
    @Sql(scripts = "file:src/test/java/ru/yandex/practicum/javafilmorate/TestResources/schema2.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "file:src/test/java/ru/yandex/practicum/javafilmorate/TestResources/data2.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getUserRecommendationsFromStorage() {
        List<Film> recFilm = userService.getUserRecommendationsFromStorage(1);
        Film film3Expected = Film.builder().id(3)
                .name("Titanic23")
                .description("Test description23")
                .duration(Duration.ofMinutes(90))
                .releaseDate(LocalDate.of(1997, 1, 27))
                .mpa(new Mpa(1, "G"))
                .build();
        List<Film> expectedList = new ArrayList<>();
        expectedList.add(film3Expected);
        assertEquals(expectedList.get(0).getId(), recFilm.get(0).getId());
    }
}