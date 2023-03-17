package ru.yandex.practicum.javafilmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

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
