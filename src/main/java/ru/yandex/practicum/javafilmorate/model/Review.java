package ru.yandex.practicum.javafilmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Модель данных (DTO) для работы с отзывами.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long reviewId;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    @NotNull
    private String content;
    @NotNull
    @JsonProperty("isPositive")
    private Boolean isPositive;
    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("useful", useful);
        return values;
    }

}
